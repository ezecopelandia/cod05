import controlP5.*;

class GuiP5 extends ControlP5{
  
  //HashMap<String, Tab> pestanas = new HashMap<String, Tab>();
  
  GuiDesequilibrio guiDesequilibrio;
  GuiCerrado guiCerrado;
  GuiNivel guiNivel;
  
  public GuiP5( PApplet p5, String[] pestanas ){
    super( p5 );
    
    PFont fuente = loadFont( "MyriadPro-Regular-14.vlw" );
    setFont( fuente );
    
    //------------ PESTANAS
    for( int i = 1; i < pestanas.length; i++ ){
      addTab( pestanas[ i ] )
      .activateEvent( true )
      .setId( i )
      .setWidth( (width / pestanas.length) - 5 )
      .getCaptionLabel().alignX( ControlP5.CENTER )
      ;
    }
    
    Tab t = getTab("default");
    t.setLabel( pestanas[ 0 ] );
    t.activateEvent( true );
    t.setId( 0 );
    t.setWidth( width / pestanas.length );
    t.getCaptionLabel().alignX( ControlP5.CENTER );
    //------------
    
    //PESTANA 0 - DEFAULT CAMARA COMUN
    
    addTextlabel("espejo")
    .setText("Espejo hacia\nCod05Mundo")
    .setPosition( width - 150, height * 0.5 - 75 )
    .setHeight( 150 );

    addToggle("espejoEjeX")
     .setPosition(width - 150, height * 0.5 - 25)
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeX() )
     .setLabel( "Espejo eje X" )
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
     
     addToggle("espejoEjeY")
     .setPosition(width - 150, height * 0.5 + 25)
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeY() )
     .setLabel( "Espejo eje Y" )
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
    
    //PESTANA 1 - DEBUG DESEQUILIBRIO
    guiDesequilibrio = new GuiDesequilibrio( this, pestanas[ 1 ] );
    
    //PESTANA 2 - DEBUG NIVEL
    guiNivel = new GuiNivel( this, pestanas[ 2 ] );
    
    //PESTANA 3 - DEBUG CERRADO
    guiCerrado = new GuiCerrado( this, pestanas[ 3 ] );
    
    //PESTANA 4 - DEBUG ESPALDA
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //seters y geters
  public void setPestanaActiva( String pestana ){
    if( !pestana.equals( motor.NOMBRE_ESTADO[ 0 ] ) ){
      getTab( pestana ).bringToFront();
    }else{
      getTab( "default" ).bringToFront();
    }
  }
 
}

void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    motor.setEstado( evento.getTab().getId() );
  }else if( evento.isFrom( "umbralesDesequilibrio" ) ){
    
    UsuarioDesequilibrio.setUmbralMenor( evento.getController().getArrayValue( 0 ) );
    UsuarioDesequilibrio.setUmbralMaximo( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
  
  }else if( evento.isFrom( "umbralesNivel" ) ){
    
    UsuarioNivel.setFactorUmbralBajo( evento.getController().getArrayValue( 0 ) );
    UsuarioNivel.setFactorUmbalAlto( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
    
  }
}

void espejoEjeX(boolean valor) {
  println("Eje X invertido:", valor);
  comunicacionOSC.setInvertidoEjeX( valor );
  saveDatosXML();
}

void espejoEjeY(boolean valor) {
  println("Eje Y invertido:", valor);
  comunicacionOSC.setInvertidoEjeY( valor );
  saveDatosXML();
} 
