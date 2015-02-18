/**
 * Juego
 *
 *  Juego en el que Juanito busca atrapar a Chimpys
 *
 * @author José Fernando Dávila Orta 999281
 * @version 01
 * @date 17/Feb/2015
 */
 
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author José Fernando Dávila
 */
public class Juego extends Applet implements Runnable,KeyListener, MouseListener,
                                    MouseMotionListener {

    private final int iMAXANCHO = 10; // maximo numero de personajes por ancho
    private final int iMAXALTO = 8;  // maxuimo numero de personajes por alto
    private Base basPrincipal;         // Objeto principal
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private AudioClip adcSonidoChimpy;   // Objeto sonido de Chimpy
    private AudioClip adcSonidoDiddy;   // Objeto sonido de Diddy
    
    
    private LinkedList<Base> lklChimpy; //lista de Chimpy
    private LinkedList<Base> lklDiddy; //lista de Diddy
    
    private int iVidas; // vidas del jugador
    private int iScore; // el score del jugador
    
    private boolean bClick; // cuando se da click
    private boolean bPause; //si se pausa el juego
    
    private int iPosX; // guarda la x del mouse
    private int iPosY; // guarda la y del mouse
    
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     * 
     */
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(800,500);
             
        // inicializa las vidas entre 5 y 7
        iVidas=(int) (Math.random() * 2) + 5;
        
        
        // se inicializa la variable en falso
        bClick=false;
        
        // se inicializa el score en 0
        iScore=0;
        
	URL urlImagenPrincipal = this.getClass().getResource("juanito.gif");
                
        // se crea el objeto para principal 
	basPrincipal = new Base(0, 0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la casilla 3,3 
        basPrincipal.setX(3 * ( getWidth() / iMAXANCHO));
        basPrincipal.setY(2 * (getHeight() / iMAXALTO));
        
        
        
        // se crea el objeto para malo 
        int iPosX = (iMAXANCHO - 1) * getWidth() / iMAXANCHO;
        int iPosY = (iMAXALTO - 1) * getHeight() / iMAXALTO;        
	
        
        // creo la lista de Chimpy
        lklChimpy = new LinkedList<Base>();
        
        // creo la lista de Diddy
        lklDiddy = new LinkedList<Base>();
        
        // genero un numero azar de 5 a 8
        int iAzar = (int) (Math.random() * 3) + 5;
        
        Base basChimpy;   // Objeto Chimpy
        Base basDiddy;   // Objeto Diddy
       
        // defino la imagen de los chimpys
	URL urlImagenChimpy = this.getClass().getResource("chimpy.gif");
        
        // defino la imagen de los Diddys
	URL urlImagenDiddy = this.getClass().getResource("diddy.gif");
        
        // genero cada Chimpy y lo añado a la lista
        for (int iI = 0; iI < iAzar; iI ++) {
           
            // se crea el objeto para chimpy
            basChimpy = new Base(iPosX,iPosY, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenChimpy));
            
            iPosX = (int) (Math.random() * iMAXANCHO);    
            iPosY = (int) (Math.random() * 
                    (getHeight()- basChimpy.getAlto())); 
        
            // se reposiciona Chimpy
            basChimpy.setX( iPosX * (getWidth() / iMAXANCHO) );
            basChimpy.setY( 0- iPosY - basChimpy.getAlto());
            
            
            // se agrega a la lista
            lklChimpy.add(basChimpy);
        }
        
         // genero cada Diddy y lo añado a la lista
        for (int iI = 0; iI < iAzar; iI ++) {
           
            // se crea el objeto para Diddy
            basDiddy = new Base(iPosX,iPosY, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenDiddy));
            
            iPosX = (int) (Math.random() * iMAXANCHO);       
            iPosY = (int) (Math.random() * 
                    (getHeight()- basDiddy.getAlto())); 
            
            // se reposiciona Diddy
            basDiddy.setX( iPosX * (getWidth() / iMAXANCHO));
            basDiddy.setY( getHeight() + iPosY + basDiddy.getAlto());
            
            // se agrega a la lista
            lklDiddy.add(basDiddy);
        }
        
        
        
        
        URL urlSonidoChimpy = this.getClass().getResource("monkey1.wav");
        adcSonidoChimpy = getAudioClip (urlSonidoChimpy);
        
        
        URL urlSonidoDiddy = this.getClass().getResource("monkey2.wav");
        adcSonidoDiddy = getAudioClip (urlSonidoDiddy);
        
        /* se le añade la opcion al applet de ser escuchado por los eventos
           del teclado y mouse  */
	addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        /* mientras dure el juego, se actualizan posiciones de jugadores
           se checa si hubo colisiones para desaparecer jugadores o corregir
           movimientos y se vuelve a pintar todo
        */ 
        while (iVidas>0) { // mientras existan vidas
            if(!bPause){ // si se pausa el juego
                actualiza();
                checaColision();
                repaint();
            }
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion de los objetos 
     * 
     */
    public void actualiza(){

     
        // mueve cada chimpy
        for (Base basChimpy : lklChimpy) {
            
            
            //Dibuja la imagen del objeto en el Applet
            basChimpy.setY(basChimpy.getY() + (5 - (iVidas / 2)));
        }
        
        // mueve cada diddy
        for (Base basDiddy : lklDiddy) {
            
            
            //Dibuja la imagen del objeto en el Applet
            basDiddy.setY(basDiddy.getY() - (5 - (iVidas / 2)));
        }
        
        // si se hace click en el mouse
        if(bClick){
            
            
            // obtiene la posicion del centro de juanito
            int iCenterX = basPrincipal.getX() + (basPrincipal.getAncho() / 2);
            int iCenterY = basPrincipal.getY() + (basPrincipal.getAlto() / 2);
            
            //cuando se da click arriba izq de juanito
            if(iPosX < iCenterX && iPosY < iCenterY){
                
                
                basPrincipal.setX(basPrincipal.getX() - (getWidth() / iMAXANCHO));
                basPrincipal.setY(basPrincipal.getY() - (getHeight() / iMAXALTO));
                
            }
            //cuando se da click arriba der de juanito
            if(iPosX > iCenterX && iPosY < iCenterY){
                
                basPrincipal.setX(basPrincipal.getX() + (getWidth() / iMAXANCHO));
                basPrincipal.setY(basPrincipal.getY() - (getHeight() / iMAXALTO));
                
            }
            //cuando se da click abajo izq de juanito
            if(iPosX < iCenterX && iPosY > iCenterY){
                
                basPrincipal.setX(basPrincipal.getX() - (getWidth() / iMAXANCHO));
                basPrincipal.setY(basPrincipal.getY() + (getHeight() / iMAXALTO));
                
            }
            //cuando se da click abajo der de juanito
            if(iPosX > iCenterX && iPosY > iCenterY){
                
                basPrincipal.setX(basPrincipal.getX() + (getWidth() / iMAXANCHO));
                basPrincipal.setY(basPrincipal.getY() + (getHeight() / iMAXALTO));
                
            }
            // se establece blick en falso
            bClick=false;
        }
        
        
    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision entre objetos
     * 
     */
    public void checaColision(){

        
        //checa las coliciones de Chimpy
        for (Base basChimpy : lklChimpy) {
         
            //cuando choca con la pared de abajo
            if(basChimpy.getY() > getHeight()){
                
                int iPosX = (int) (Math.random() * iMAXANCHO);    
               
                int iPosY = (int) (Math.random() * 
                    (getHeight()- basChimpy.getAlto())); 
            
                // se reposiciona chimpy
                basChimpy.setX(iPosX * (getWidth() / iMAXANCHO));
                basChimpy.setY( 0- iPosY - basChimpy.getAlto());
                
                
            }
            
            //checo si el juanito atrapa a Chimpy
            if (basPrincipal.intersecta(basChimpy)) 
            {
                
                int iPosX = (int) (Math.random() * iMAXANCHO);    
                int iPosY = (int) (Math.random() * 
                    (getHeight()- basChimpy.getAlto())); 
            
                // se reposiciona Chimpy
                basChimpy.setX(iPosX * (getWidth() / iMAXANCHO));
                basChimpy.setY( 0- iPosY - basChimpy.getAlto());
                
                
                // se agrega 1 punto al score
                iScore++;
                
                // reproduce sonido de Chimpy
                adcSonidoChimpy.play();
            }
            
            
        }
        
        // checa las coliciones de Diddy
        for (Base basDiddy : lklDiddy) {
         
            // cuando choca con la pared de arriba
            if(basDiddy.getY()+ basDiddy.getAlto() < 0){
                
                int iPosX = (int) (Math.random() * iMAXANCHO);    
                int iPosY = (int) (Math.random() * 
                    (getHeight()- basDiddy.getAlto())); 
            
                // se reposiciona Diddy
                basDiddy.setX(iPosX * (getWidth() / iMAXANCHO));
                basDiddy.setY( getHeight() + iPosY + basDiddy.getAlto());
                
               
            }
            
            //checo si el juanito es capturado por Diddy
            if (basPrincipal.intersecta(basDiddy)) 
            {
                
                // vuelve a dibujar a Diddy
                int iPosX = (int) (Math.random() * iMAXANCHO);    
                int iPosY = (int) (Math.random() * 
                    (getHeight()- basDiddy.getAlto())); 
            
                // se reposiciona Diddy
                basDiddy.setX(iPosX * (getWidth() / iMAXANCHO));
                basDiddy.setY( getHeight() + iPosY + basDiddy.getAlto());
                
                
                // se restan vidas
                iVidas--;
                // reproduce sonido de Diddy
                adcSonidoDiddy.play();
            }
            
            
        }
        
        
        // si juanito se quiere salir del applet
        
        if(basPrincipal.getX() < 0){
            basPrincipal.setX(0);
            
        }else if(basPrincipal.getY() + basPrincipal.getAlto() > getHeight()){
            
            basPrincipal.setY(getHeight() - basPrincipal.getAlto());
            
           
        } else if(basPrincipal.getX() + basPrincipal.getAncho() > getWidth()){
            
            basPrincipal.setX(getWidth() - basPrincipal.getAncho());
            
        }else if(basPrincipal.getY() < 0){
            
            basPrincipal.setY(0);
            
           
        }
        
        
        
        
    }
    
    
	
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * 
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void update (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("Ciudad.png");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
         graGraficaApplet.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);

   
        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * 
     * @param graDibujo es el objeto de <code>Graphics</code> usado para dibujar.
     * 
     */
    public void paint(Graphics graDibujo) {
        
        //si se acaban las vidas muestra el GAME OVER
        if(iVidas==0){
            
            graDibujo.drawString("GAME OVER", getWidth()/2-210, getHeight()/2);
            graDibujo.setFont(graDibujo.getFont().deriveFont(60.0f));
        }else{
            // si aun existen vidas muestra cuantas
            graDibujo.drawString("Vidas: "+ iVidas, 3*(getWidth()/4)-50, 30);
            
            graDibujo.drawString("Puntos: "+ iScore, 3*(getWidth()/4)-50, 65);   
            graDibujo.setFont(graDibujo.getFont().deriveFont(30.0f));
        }
        
        // si la imagen ya se cargo
        if (basPrincipal != null && lklChimpy != null && lklDiddy != null) {
                //Dibuja la imagen de principal en el Applet
                basPrincipal.paint(graDibujo, this);
                
                
                // pinto cada Chimpy de la lista en el Applet
                for (Base basChimpy : lklChimpy) {
                    //Dibuja la imagen del fantasma en el Applet
                    basChimpy.paint(graDibujo, this);
                }
                
                // pinto cada Diddy de la lista en el Applet
                for (Base basDiddy : lklDiddy) {
                    //Dibuja la imagen del fantasma en el Applet
                    basDiddy.paint(graDibujo, this);
                }
                
                
        } // sino se ha cargado se dibuja un mensaje 
        else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mseEvent) {
        
        bClick=true;
        
        //la posicion del mouse
        iPosX = mseEvent.getX();
        iPosY = mseEvent.getY();
        
    }

    @Override
    public void mousePressed(MouseEvent mseEvent) {
        
    }

    @Override
    public void mouseReleased(MouseEvent mseEvent) {
        bClick=false;
    }

    @Override
    public void mouseEntered(MouseEvent mseEvent) {
        
    }

    @Override
    public void mouseExited(MouseEvent mseEvent) {
        
    }

    @Override
    public void mouseDragged(MouseEvent mseEvent) {
        
    }

    @Override
    public void mouseMoved(MouseEvent mseEvent) {
        
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        
        if(keyEvent.getKeyCode() == KeyEvent.VK_P) {    
            
            if(bPause){
                bPause=false;
            }else{
                bPause=true;
            }
              
        }else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {    
            
            iVidas=0;
              
        }
        
        
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        
    }
}