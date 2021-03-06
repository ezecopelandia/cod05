class Interfaz implements AutoSetup, AutoDraw {
  ConfiguracionCOD05 config;
  BotonBasico botonPlay;
  BotonModulo lienzo, observador, carrete;
  InterfazYSensorConexion interfazYSensorConexion;
  BarraSuperior barraSuperior;
  TwOutQuad animTodoGris;
  Ejecutador ejecutadorLocal;//vive hasta que se mueran sus procesos
  boolean todoLocal = true;
  int pingOff = 4000;
  float pingTimer = 4, pingFrec = 1;

  Interfaz() {
    autoSetup.add(this);
    autoDraw.add(this);
    animTodoGris = (TwOutQuad)(new TwOutQuad()).inicializar(.9, 0, 3);
  }
  void setup() {
    {
      float verti = height/2;
      float sepHoriz = 110;
      lienzo = new BotonModulo(new PVector(width/2-sepHoriz, verti), dicIcos.lienzo, paleta.ips[0]);
      observador = new BotonModulo(new PVector(width/2, verti), dicIcos.observador, paleta.ips[1]);
      carrete = new BotonModulo(new PVector(width/2+sepHoriz, verti), dicIcos.carrete, paleta.ips[2]);
    }
    interfazYSensorConexion = new InterfazYSensorConexion();
    barraSuperior = new BarraSuperior();
    cargarDatos();

    botonPlay = new BotonBasico(90, height-90, 0, "play", paleta.play);
    botonPlay.escala = .6f;
    botonPlay.hoverEscala = new TwOutBack().inicializar(.25, 1, 1.1, 0);
    botonPlay.toggleAlfa = new TwOutQuad().inicializar(.25, 255, 25, 0);
  }
  void draw() {
    if (introActiva)intro();
    else {
      //interfazYSensorConexion.visible = !lienzo.local || !observador.local || !carrete.local;
      todoLocal = lienzo.estado == EstadoModulo.LOCAL && observador.estado == EstadoModulo.LOCAL && carrete.estado == EstadoModulo.LOCAL;
      grisPorTodoLocal();
      pingTesting();
    }

    lienzo.colEncendido = interfazYSensorConexion.lienzo.col;
    observador.colEncendido = interfazYSensorConexion.observador.col;
    carrete.colEncendido = interfazYSensorConexion.carrete.col;

    if (ejecutadorLocal != null) {
      if (!ejecutadorLocal.enEjecucion()) {
        ejecutadorLocal = null;
        botonPlay.toggle = false;
      } else {
        botonPlay.toggle = true;
      }
    } else if (botonPlay.presionado && botonPlay.toggle) ejecutar();
  }

  void ejecutar() {
    guardarDatos();
    ejecutadorLocal = new Ejecutador(config, modoPDE);
    ejecutadorLocal.ejecutarLocales();
    controlOsc.ejecutarRemotos(config);
  }

  void cargarDatos() {
    if (config == null) config = new ConfiguracionCOD05();
    XML xmlConfig = null;
    if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
    if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagPanel);

    //config.cargar(xmlConfig);
    //-------- para cargar local
    //--- :D esto no tien que ver con le cambio feo de el boton basico peor igual lo comento
    //---- para que siempre empiece en localhost y se vea como en la documentacion
    //---- comente el cargar con el xml y cree en metodo que crea los campos pero con info de local host
    config.cargar_local();
    lienzo.set(config.lienzo);
    observador.set(config.observador);
    carrete.set(config.carrete);
    interfazYSensorConexion.setConfig(config);
  }

  void guardarDatos() {
    if (config != null) {
      XML xmlArchivo = null;
      if (new File(sketchPath(archivoConfigXML)).exists()) xmlArchivo = loadXML( archivoConfigXML );
      if (xmlArchivo == null) xmlArchivo = new XML("COD05");
      XML xmlPanel = xmlArchivo.getChild(xmlTagPanel);
      if (xmlPanel != null) xmlArchivo.removeChild(xmlPanel);
      xmlArchivo.addChild(config.guardar(xmlTagPanel));
      saveXML(xmlArchivo, archivoConfigXML);
    }
  }

  float introTime = 0;
  boolean introActiva = true;
  void intro() {
    float introBotonModuloBase = 2, introBotonModuloSep = .05f;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*0))lienzo.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*1))observador.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*2))carrete.mostrar = true;

    introTime += dt;
    if (introTime > introBotonModuloBase+introBotonModuloSep*2) introActiva = false;
  }
  boolean introCheck(float t) {
    return introTime < t && introTime+dt >= t;
  }

  void grisPorTodoLocal() {
    carrete.panelIPsAbierto = observador.panelIPsAbierto = lienzo.panelIPsAbierto = interfazYSensorConexion.visible;
    todoLocal = todoLocal && !interfazYSensorConexion.visible;
    animTodoGris.actualizar(todoLocal?dt:-dt);
    carrete.todoGris = constrain(animTodoGris.valor()-2, 0, 1);
    observador.todoGris = constrain(animTodoGris.valor()-1, 0, 1);
    lienzo.todoGris = constrain(animTodoGris.valor(), 0, 1);
  }

  void pingTesting() {
    pingTimer -= dt;
    if (pingTimer <= 0) {
      pingTimer = pingFrec;
      if (config.lienzo.estado==EstadoModulo.REMOTO) {
        controlOsc.pingLienzo(config.lienzo.ip, config.lienzo.puerto);
        if (!lienzo.remotoEncontrado) {
          lienzo.animAro.actualizar(-.3);
        }
      }
      if (config.observador.estado==EstadoModulo.REMOTO) {
        controlOsc.pingObservador(config.observador.ip, config.observador.puerto);
        if (!observador.remotoEncontrado) {
          observador.animAro.actualizar(-.3);
        }
      }
      if (config.carrete.estado==EstadoModulo.REMOTO) {
        controlOsc.pingCarrete(config.carrete.ip, config.carrete.puerto);
        if (!carrete.remotoEncontrado) {
          carrete.animAro.actualizar(-.3);
        }
      }
    }

    lienzo.remotoEncontrado = millis()-controlOsc.ultimoPingLienzo <= pingOff;
    observador.remotoEncontrado = millis()-controlOsc.ultimoPingObservador <= pingOff;
    carrete.remotoEncontrado = millis()-controlOsc.ultimoPingCarrete <= pingOff;
  }
}