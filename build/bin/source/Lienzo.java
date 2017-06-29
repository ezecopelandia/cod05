import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 
import java.util.Map; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Lienzo extends PApplet {

//-------------------------------------
//resolver un ropblema con el flock y quiza con otro modificadores
//cuando algun modificador no esta funcional puede que algunos aributos se modifiquen constantemente y cuadno se activa el modificador pasan cosas malas....
//ejemplo cuando mover se desactiva el flock sigue aumentando la aceleracion que no se resetea como deberia....
//opciones ubicar el reseteo de la aceleracion como parte constante de la existencia del mismo... o algo parecido....





OscP5 oscP5;
NetAddress consola;

Sistema sistema;
ManagerUsuarios managerUsuarios;

boolean pausa; 
boolean fondoAlfa;

int posModificadorMover;
int raizDeCantidad = 12;//12

//si los metodos de la clase maquinarias son static podria no ser necesario crear una instancia
Maquinarias maquinarias;
XML xmlMaquinarias;

public void setup() {
  
  
  
  //size(displayWidth, displayHeight);
  //size(displayWidth/2, displayHeight/2);
  //size( 800, 600 );
  
  //sketchFullScreen();
  
  noCursor();
 
  xmlMaquinarias  = loadXML("maqs.xml"); 
  maquinarias = new Maquinarias(xmlMaquinarias);
  
  managerUsuarios = new ManagerUsuarios();
  
  sistema = new Sistema(this, raizDeCantidad*raizDeCantidad, managerUsuarios);
  
  //if (sistema.registroModificadores==null)sistema.registroModificadores = new HashMap();
  //println(sistema.registroModificadores);
  println(Mod_AlfaSegunVelocidad.registrador);
  println(Mod_AlfaSegunCercania.registrador);
  //Mod_AlfaSegunVelocidad.velocidadMayor = 5;
  println(Mod_AtraccionAlCentro.registrador);
  //Mod_AtraccionAlCentro.factor = .001f;
  println(Mod_ColisionSimple.registrador);
  println(Mod_DibujarCirculo.registrador);
  println(Mod_DibujarCuadrado.registrador);
  println(Mod_DibujarFlecha.registrador);
  println(Mod_DibujarReas.registrador);
  println(Mod_EspacioCerrado.registrador);
  println(Mod_EspacioToroidal.registrador);
  println(Mod_FlockAlineamiento.registrador);
  println(Mod_FlockCohesion.registrador);
  println(Mod_FlockSeparacion.registrador);
  println(Mod_FuerzasPorSemejanza.registrador);
  println(Mod_Gravedad.registrador);
  println(Mod_Mover.registrador);
  println(Mod_ResetLluvia.registrador); 
  println(Mod_RastroElastico.registrador);
  println(Mod_DibujarRastroCircular.registrador);
  println(Mod_DibujarRastroCuadrado.registrador);
  println(Mod_DibujarRastroShape.registrador);
  println(Mod_AtraccionALaMano.registrador);
  println(Mod_Egoespacio.registrador);
  println(Mod_PaletaDefault.registrador);
  println(Mod_PaletaPersonalizada.registrador);
  //Atr_Tamano.inicialMinimo2Dados = 1;
  // Atr_Tamano.inicialMaximo2Dados = 20;

  //sistema.agregar(Mod_AlfaSegunVelocidad.registrador) .velocidadMayor = 100;
  //sistema.agregar(Mod_FlockSeparacion.registrador);
  //sistema.agregar(Mod_FlockCohesion.registrador);
  //sistema.agregar(Mod_FlockAlineamiento.registrador);
  //sistema.agregar(Mod_EspacioToroidal.registrador.key());
  //sistema.agregar(Mod_FuerzasPorSemejanza.registrador);
  //sistema.agregar(Mod_AtraccionAlCentro.registrador);
  //sistema.agregar(Mod_ColisionSimple.registrador.key());
  //sistema.agregar(Mod_Mover.registrador.key());
  //sistema.agregar(Mod_DibujarFlecha.registrador);
  //sistema.agregar(Mod_DibujarReas.registrador).factorTamanio = 1f;
  //sistema.agregar(Mod_DibujarCirculo.registrador.key());
  //sistema.agregar(Mod_RastroElastico.registrador.key());
  //sistema.agregar(Mod_DibujarRastroCircular.registrador.key());
  //sistema.agregar(Mod_RastroElastico.registrador.key());
  //sistema.agregar(Mod_DibujarRastroShape.registrador.key());
  //sistema.agregar(Mod_DibujarFlecha.registrador.key());
  //---------------------------------------------------------------------------MODIFICADORES TOTAL----------------------------------------------------------------------------

  initOSC();
  modificadoresExistentes();

  //modificadoresTotal();
  for (String n : sistema.registroModificadores.keySet ()) {
    String categoria = sistema.registroModificadores.get(n).categoria();
    // println(categoria);
  }
  
}

public void draw() {
  if (!pausa) {
    ciclo();
  }
}

public void ciclo() { 
  if (fondoAlfa) {
    pushStyle();
    fill(0, 5);
    rect(0, 0, width, height);
    popStyle();
  } else {
    background(0);
  }

  sistema.actualizar();
  managerUsuarios.actualizar();

  fill(255);
  text(frameRate, 5, 10);
  
  if( sistema.debug ){
    text( "DEBUG", 5, 30 );
    managerUsuarios.debug( this );
  }
  
}

/*
boolean sketchFullScreen() {
  return true;
}
*/

public void keyPressed() {

  if (key == ' ') pausa = !pausa;
  else if (keyCode == TAB) ciclo();
  else if (keyCode == BACKSPACE || keyCode == DELETE) sistema.reset();
  else if (key == 'f') fondoAlfa = !fondoAlfa;
  else if (key == 'd') sistema.debug = !sistema.debug;
  else if (key == 's') saveFrame("capturas/capturas_"+frameCount+".png");
  else if (key == 'v') setMaquinaria(maquinarias.getListaMaquinarias()[0]);
}
ConfiguracionCOD05 config;
int cantidadOpciones = 5;

String[] opciones = new String[cantidadOpciones];

public void initOSC() {
  if (config == null) config = new ConfiguracionCOD05();
  XML xmlConfig = null;
  if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
  if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagEjecucion);

  config.cargar(xmlConfig);

  cargarOpciones();

  noSmooth();
  noStroke();

  oscP5 = new OscP5(this, config.lienzo.puerto);
  consola = new NetAddress(config.carrete.ip, config.carrete.puerto);

  oscP5.plug(this, "opciones", "/pedir/opciones");
  oscP5.plug(this, "accionOpciones", "/accion/opciones");

  oscP5.plug(this, "modificadoresTotal", "/pedir/modificadores/total");
  oscP5.plug(this, "modificadoresExistentes", "/pedir/modificadores/existentes");
  oscP5.plug(this, "modificadoresAgregar", "/agregar/modificadores");
  oscP5.plug(this, "modificadoresQuitar", "/quitar/modificadores");

  oscP5.plug(this, "maquinarias", "/pedir/maquinarias");
  oscP5.plug(this, "setMaquinaria", "/set/maquinaria");

  oscP5.plug(this, "recibirUsuarioJoint", "/enviar/usuario/joint");
  oscP5.plug(this, "removerUsuario", "/remover/usuario" );
  //oscP5.plug(this, "enviarEstimulos", "/pedir/estimulos");
}

public void accionOpciones(String cual) {
  if (cual.equals("pausa")) pausa = !pausa;
  if (cual.equals("ciclo")) ciclo();
  if (cual.equals("reset")) sistema.reset();
  if (cual.equals("fondoAlfa")) fondoAlfa = !fondoAlfa;
  if (cual.equals("pausa")) sistema.debug = !sistema.debug;
}

public void cargarOpciones() {
  opciones[0] = "pausa";
  opciones[1] = "ciclo";
  opciones[2] = "reset";
  opciones[3] = "fondoAlfa";
  opciones[4] = "sistema.debug";
}

public void opciones() {
  mensaje_CANTIDAD("/opciones", cantidadOpciones);
  for (int i = 0; i< cantidadOpciones; i++) {
    mensaje_NOMBRE_ESTADO("/opciones", opciones[i], 0);
  }
  mensaje("/opciones/listo") ;
}

public void modificadoresTotal() {
  // println("paso por aqui");
  mensaje_CANTIDAD("/modificadores/totales", sistema.registroModificadores.size());
  // println(sistema.registroModificadores.size());
  //for (int i = 0; i< sistema.registroModificadores.size(); i++) {
  for (String n : sistema.registroModificadores.keySet ()) {
    String categoria = sistema.registroModificadores.get(n).categoria();
    //println(categoria);
    mensaje_NOMBRE_CATEGORIA_ESTADO("/modificadores/totales", n, categoria, 0);
  }
  mensaje("/modificadores/totales/listo");
}

public void modificadoresExistentes() {
  String[] lista = sistema.getDirectorioModificadores();
  mensaje_CANTIDAD("/modificadores/existentes", lista.length);
  //int contador = 0;
  for (int i = 0; i< sistema.getCantidadModificadores (); i++) {
    //String categoria = sistema.registroModificadores.get(lista[i]).categoria();
    //contador++;
    //println()
    mensaje_NOMBRE_ESTADO("/modificadores/existentes", lista[i], sistema.getEstado(lista[i])? 1 : 0);
  }
  mensaje("/modificadores/existentes/listo");
}

public void modificadoresAgregar(String cual) {
  if (sistema.agregar(cual)!=null) {
    OscMessage msj = new OscMessage("/agregarMod");
    msj.add(cual);
    oscP5.send(msj, consola);
  }
}

public void modificadoresQuitar(String cual) {
  if (sistema.eliminar(cual)!=null) {
    OscMessage msj = new OscMessage("/quitarMod");
    msj.add(cual);
    oscP5.send(msj, consola);
  }  //modificadoresExistentes();
}



public void maquinarias() {   
  mensaje_CANTIDAD("/maquinarias", sistema.registroModificadores.size());
  for (String n : maquinarias.getListaMaquinarias()) {
    mensaje_NOMBRE("/maquinarias", n);
  }
  mensaje("/maquinarias/listo");
}
//variable global que solo sirve para anular la posibilidad de modificar 
//los modificadores activos mientras el sistema esta vaciandolos
//asi se evita borrarlos dos veces si se envia el mensaje multiples ocaciones
boolean vaciandoModificadores;
public void setMaquinaria(String nombre) {  
  println("empieza mensaje");
  //------------------------ LIMPIAR LOS MODIFICADORES ACTIVOS -------------------
  String listaParaEliminar = sistema.modificadoresActivos_lista();
  if ( listaParaEliminar != null && !vaciandoModificadores) {     
    vaciandoModificadores = true;
    sistema.vaciarModificadores();     
    OscMessage msj = new OscMessage("/quitarListaMod");
    println("datos = "+listaParaEliminar);
    println("separador = "+sistema.separadorMaquinarias);
    if (listaParaEliminar!=null) {
      msj.add(listaParaEliminar);
      msj.add(sistema.separadorMaquinarias);
      println("envia mensaje");
      oscP5.send(msj, consola);
    }
  }
  vaciandoModificadores = false;

  //------------------------ PRENDER LOS MODIFICADORES -------------------
  String[] listaModsNuevos = maquinarias.getMaquinaria(nombre);
  for (int i=0; i<listaModsNuevos.length; i++) {
    println("agregando:" + listaModsNuevos[i]);
    if (sistema.agregar(listaModsNuevos[i])!=null) {
      OscMessage msj = new OscMessage("/agregarMod");
      msj.add(listaModsNuevos[i]);
      oscP5.send(msj, consola);
    }
  }
}

public void recibirUsuarioJoint(int keyUsuario, String nombre_joint, float x, float y, float confianza ) {
  managerUsuarios.setUsuarioJoint( keyUsuario, nombre_joint, x, y, confianza );
}

public void removerUsuario( int keyUsuario ) {
  println("EN LA PESTANA OSC ESTA COMENTADO REMOVER USUARIO");
  //managerUsuarios.removerUsuario( keyUsuario );
}

public void mensaje(String mensaje) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  oscP5.send(mensajeModificadores, consola);
}

public void mensaje_CANTIDAD(String mensaje, int cantidad) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(cantidad);
  oscP5.send(mensajeModificadores, consola);
}

public void mensaje_NOMBRE(String mensaje, String nombre) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(nombre);
  oscP5.send(mensajeModificadores, consola);
}

public void mensaje_NOMBRE_ESTADO(String mensaje, String nombre, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(nombre);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}

public void mensaje_NOMBRE_CATEGORIA_ESTADO(String mensaje, String nombre, String categoria, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(nombre);
  mensajeModificadores.add(categoria);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}


public void mensaje_POSICION_ESTADO(String mensaje, int posicion, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(posicion);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}
// The following short XML file called "mammals.xml" is parsed 
// in the code below. It must be in the project's "data" folder.
//
// <?xml version="1.0"?>
// <maquinarias>
//   <maquinaria id="0" nombre="Lumiere">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="1" nombre="Cohl">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="2" nombre="Melies">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="3" nombre="Guy Blach\u00e9">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
// </maquinarias>

/*
"Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
 "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
 "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
 "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
 "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
 "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
 "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad"
 */



class Maquinarias {

  int cantidad;
  // ArrayList maquinarias<Maquinarias> = new ArrayList<Maquinarias>();  
  HashMap<String, String[]> registroMaquinarias = new HashMap<String, String[]>();
  String[] listaMaquinarias;
  char separador = '|';
  Maquinarias(XML xml) {

    XML[] children = xml.getChildren("maq");
    listaMaquinarias = new String[children.length];
    for (int i = 0; i < children.length; i++) {
      int id = children[i].getInt("id");
      String nombre = children[i].getString("nombre");
      String modificadores = children[i].getContent();
      listaMaquinarias[i] = nombre;
      String[ ]mods = split(modificadores, separador);
      registroMaquinarias.put(nombre, mods);

      /* Maquinaria maq = new Maquinaria(id, nombre, modificadores, separador);
       maquinarias.add(maq);*/
    }
  }

  public String[] getListaMaquinarias() {
    return listaMaquinarias;
  }

  public String[] getMaquinaria(String nombre) {
    String[] listaMods = registroMaquinarias.get(nombre);
    return listaMods;
  }
}
/*class Maquinaria {
 int id;
 String nombre;
 String[] modificadores;
 Maquinaria(int id_, String nombre, String modificadores, char separador) {
 id = id_;
 nombre = nombre_;
 }
 
 getMods() {
 }
 }*/

// Sketch prints:
// 0, Capra hircus, Goat
// 1, Panthera pardus, Leopard
// 2, Equus zebra, Zebra
//v 22/06/2017
String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

enum EstadoModulo {
  APAGADO, LOCAL, REMOTO
}
final EstadoModulo[] EstadoModuloList = new EstadoModulo[]{EstadoModulo.APAGADO, EstadoModulo.LOCAL, EstadoModulo.REMOTO};
public int EstadoModuloToInt(EstadoModulo estado) {return estado==EstadoModulo.APAGADO?0:estado==EstadoModulo.LOCAL?1:2;};

class ConfiguracionCOD05 {
  ConfigModulo lienzo, observador, carrete;
  boolean panelConexiones = false;

  class ConfigModulo {
    String id = "indefinido";
    String ip = "127.0.0.1";
    int puerto = 12000;
    EstadoModulo estado = EstadoModulo.LOCAL;

    public ConfigModulo Iniciar(String id, int puerto) {
      this.id = id;
      this.puerto = puerto;
      return this;
    }

    public void cargar(XML xml) {
      id = xml.getString("id", id);
      ip = xml.getString("ip", ip);
      puerto = xml.getInt("puerto", puerto);
      int estadoInt = xml.getInt("estado", -1);
      if (estadoInt != -1) estado = EstadoModuloList[estadoInt];
    }    
    public XML generar() {
      XML xml = new XML("ConfigModulo");
      xml.setString("id", id);
      xml.setString("ip", ip);
      xml.setInt("puerto", puerto);
      xml.setInt("estado", EstadoModuloToInt(estado));
      return xml;
    }
  }
  public void cargar(XML xml) {
    lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    observador = new ConfigModulo().Iniciar("observador", 12020);
    carrete = new ConfigModulo().Iniciar("carrete", 12030);
    if (xml != null) {
      panelConexiones = xml.getInt("panelConexiones", panelConexiones?1:0)==1;
      XML[] configs = xml.getChildren("ConfigModulo");
      for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
        for (XML cxml : configs) {
          if (cm.id.equals(cxml.getString("id", ""))) cm.cargar(cxml);
        }
      }
    }
  }
  public XML guardar(String nombre) {
    XML xml = new XML(nombre);
    xml.setInt("panelConexiones", panelConexiones?1:0);
    for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
      xml.addChild(cm.generar());
    }
    return xml;
  }
}

   Registrador<Mod_FriccionGlobal> regFriccionGlobal = new Registrador(){
    public String key() {return "Friccion Global";}
     public String categoria() {return "Aplicar Fuerza";}
    public Mod_FriccionGlobal generarInstancia(){return new Mod_FriccionGlobal();}
  };

class Mod_FriccionGlobal extends Modificador {
  float factor = .001f;

  public void ejecutar(Sistema s) {
    //Atr_Fuerza fuerzas = (Atr_Fuerza)sistema.requerir(Atr_Fuerza.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad vel = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    //Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);

    for (int i=0; i<s.tamano; i++) {
      vel.v[i].mult(factor);
      //PVector p=posiciones.p[i];
      //acel.a[i].add(PVector.mult(PVector.fromAngle(s.p5.atan2(s.p5.height/2-p.y, s.p5.width/2-p.x)), s.tamano*factor) );
      //fuerzas.f[i].add( PVector.mult(PVector.fromAngle(atan2(height/2-p.y, width/2-p.x)), sistema.tamano*factor) );
    }
  }
  
}
  public void settings() {  fullScreen( 2 ); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Lienzo" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}