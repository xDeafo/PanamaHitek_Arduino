/**
 * Este código ha sido construido por Antony García González y el Equipo
 * Creativo de Panama Hitek.
 *
 * Está protegido bajo la licencia LGPL v 2.1, cuya copia se puede encontrar en
 * el siguiente enlace: http://www.gnu.org/licenses/lgpl.txt
 *
 * Para su funcionamiento utiliza el código de la librería JSSC (anteriormente
 * RXTX) que ha permanecido intacto sin modificación alguna de parte de nuestro
 * equipo creativo. Agradecemos al creador de la librería JSSC, Alexey Sokolov
 * por esta herramienta tan poderosa y eficaz que ha hecho posible el
 * mejoramiento de nuestra librería.
 *
 * Esta librería es de código abierto y ha sido diseñada para que los usuarios,
 * desde principiantes hasta expertos puedan contar con las herramientas
 * apropiadas para el desarrollo de sus proyectos, de una forma sencilla y
 * agradable.
 *
 * Se espera que se en cualquier uso de este código se reconozca su procedencia.
 * Este algoritmo fue diseñado en la República de Panamá por Antony García
 * Gónzález, estudiante de la Universidad de Panamá en la carrera de
 * Licenciatura en Ingeniería Electromecánica, desde el año 2013 hasta el
 * presente. Su diseñador forma parte del Equipo Creativo de Panama Hitek, una
 * organización sin fines de lucro dedicada a la enseñanza del desarrollo de
 * software y hardware a través de su sitio web oficial http://panamahitek.com
 *
 * Solamente deseamos que se reconozca esta compilación de código como un
 * trabajo hecho por panameños para Panamá y el mundo.
 *
 * Si desea contactarnos escríbanos a creativeteam@panamahitek.com
 */
package com.panamahitek.liveinterfaces;

import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.*;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 * Esta clase construye un grafico tipo reloj en la que se pueden graficar datos
 * recibidos desde Arduino en tiempo real
 *
 * 
 * @author Antony García González, de Proyecto Panama Hitek. Visita
 * http://panamahitek.com

 * @since 3.0.0
 */
public class PanamaHitek_SingleDialPlot extends JPanel {

    public final static int ROUND_DIAL_PLOT = 1;
    public final static int HORIZONTAL_DIAL_PLOT = 2;
    public final static int VERTICAL_DIAL_PLOT = 3;

    private static int PLOT_SELECTION = 1;

    private static plotPanel plot;
    private static PanamaHitek_Arduino ino = null;

    private class plotPanel extends JPanel {

        //Dataset donde se guardan los datos
        private DefaultValueDataset dataset;
        //Titulo de la grafica
        private String plotTitle = "Default Title";
        //Nombre de la variable a graficar
        private String variableName = "Default Variable Name";
        private int plotBottonLimit = 0; //Limite inferior de la grafica
        private int plotTopLimit = 100; //Limite superior de la grafica
        private int minorDivisions = 4; //Divisiones menores
        private int majorDivisions = 20;//Divisiones mayores
        private int redBottomLimit = 0; //Limite inferior del color rojo
        private int redTopLimit = 0; //Limite superior del color rojo
        private int yellowBottomLimit = 0;
        private int yellowTopLimit = 0;
        private int greenBottomLimit = 0;
        private int greenTopLimit = 0;
        private double greenSlice = 0.5; //Porcentaje del color verde
        private double yellowSlice = 0.3;
        private double redSlice = 0.2;

        public DefaultValueDataset getDataset() {
            return dataset;
        }

        public void setDataset(DefaultValueDataset dataset) {
            this.dataset = dataset;
        }

        public String getPlotTitle() {
            return plotTitle;
        }

        public void setPlotTitle(String plotTitle) {
            this.plotTitle = plotTitle;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }

        public int getPlotBottonLimit() {
            return plotBottonLimit;
        }

        public void setPlotBottonLimit(int plotBottonLimit) {
            this.plotBottonLimit = plotBottonLimit;
        }

        public int getPlotTopLimit() {
            return plotTopLimit;
        }

        public void setPlotTopLimit(int plotTopLimit) {
            this.plotTopLimit = plotTopLimit;
        }

        //Calculo de los limites de los colores segun el maximo y el minimo
        private void setColorLimits() {
            int minValue = plotBottonLimit;
            int maxValue = plotTopLimit;
            setGreenBottomLimit(minValue);
            setGreenTopLimit((int) (minValue + Math.abs(maxValue - minValue) * getGreenSlice()));
            setYellowBottomLimit(getGreenTopLimit());
            setYellowTopLimit((int) (getGreenTopLimit() + (Math.abs(maxValue - minValue) * getYellowSlice())));
            setRedBottomLimit(getYellowTopLimit());
            setRedTopLimit((int) (getYellowTopLimit() + (Math.abs(maxValue - minValue) * getRedSlice())));
        }

        public int getMinorDivisions() {
            return minorDivisions;
        }

        public void setMinorDivisions(int minorDivisions) {
            this.minorDivisions = minorDivisions;
        }

        public int getMajorDivisions() {
            return majorDivisions;
        }

        public void setMajorDivisions(int majorDivisions) {
            this.majorDivisions = majorDivisions;
        }

        public int getRedBottomLimit() {
            return redBottomLimit;
        }

        public void setRedBottomLimit(int redBottomLimit) {
            this.redBottomLimit = redBottomLimit;
        }

        public int getRedTopLimit() {
            return redTopLimit;
        }

        public void setRedTopLimit(int redTopLimit) {
            this.redTopLimit = redTopLimit;
        }

        public int getYellowBottomLimit() {
            return yellowBottomLimit;
        }

        public void setYellowBottomLimit(int yellowBottomLimit) {
            this.yellowBottomLimit = yellowBottomLimit;
        }

        public int getYellowTopLimit() {
            return yellowTopLimit;
        }

        public void setYellowTopLimit(int yellowTopLimit) {
            this.yellowTopLimit = yellowTopLimit;
        }

        public int getGreenBottomLimit() {
            return greenBottomLimit;
        }

        public void setGreenBottomLimit(int greenBottomLimit) {
            this.greenBottomLimit = greenBottomLimit;
        }

        public int getGreenTopLimit() {
            return greenTopLimit;
        }

        public void setGreenTopLimit(int greenTopLimit) {
            this.greenTopLimit = greenTopLimit;
        }

        public double getGreenSlice() {
            return greenSlice;
        }

        public void setGreenSlice(double greenSlice) {
            this.greenSlice = greenSlice;
        }

        public double getYellowSlice() {
            return yellowSlice;
        }

        public void setYellowSlice(double yellowSlice) {
            this.yellowSlice = yellowSlice;
        }

        public double getRedSlice() {
            return redSlice;
        }

        public void setRedSlice(double redSlice) {
            this.redSlice = redSlice;
        }

        public JFreeChart createStandardDialChart(String s, String s1, ValueDataset valuedataset, double d, double d1, double d2, int i) {
            DialPlot dialplot = new DialPlot();
            dialplot.setDataset(valuedataset);
            dialplot.setDialFrame(new StandardDialFrame());
            dialplot.setBackground(new DialBackground());
            DialTextAnnotation dialtextannotation = new DialTextAnnotation(s1);
            dialtextannotation.setFont(new Font("Dialog", 1, 14));
            dialtextannotation.setRadius(0.7D);
            dialplot.addLayer(dialtextannotation);
            DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
            dialplot.addLayer(dialvalueindicator);
            StandardDialScale standarddialscale = new StandardDialScale(d, d1, -120D, -300D, 10D, 4);
            standarddialscale.setMajorTickIncrement(d2);
            standarddialscale.setMinorTickCount(i);
            standarddialscale.setTickRadius(0.88D);
            standarddialscale.setTickLabelOffset(0.15D);
            standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
            dialplot.addScale(0, standarddialscale);
            dialplot.addPointer(new org.jfree.chart.plot.dial.DialPointer.Pin());
            DialCap dialcap = new DialCap();
            dialplot.setCap(dialcap);
            return new JFreeChart(s, dialplot);
        }

        public plotPanel() {
            super(new BorderLayout());

        }

        public void buildPlot1() {
            setColorLimits();
            dataset = new DefaultValueDataset(10D);
            JFreeChart jfreechart = createStandardDialChart(plotTitle, variableName, dataset, plotBottonLimit, plotTopLimit, majorDivisions, minorDivisions);
            DialPlot dialplot = (DialPlot) jfreechart.getPlot();
            StandardDialRange standarddialrange = new StandardDialRange(redBottomLimit, redTopLimit, Color.red);
            standarddialrange.setInnerRadius(0.522D);
            standarddialrange.setOuterRadius(0.554D);
            dialplot.addLayer(standarddialrange);
            StandardDialRange standarddialrange1 = new StandardDialRange(yellowBottomLimit, yellowTopLimit, Color.orange);
            standarddialrange1.setInnerRadius(0.522D);
            standarddialrange1.setOuterRadius(0.554D);
            dialplot.addLayer(standarddialrange1);
            StandardDialRange standarddialrange2 = new StandardDialRange(greenBottomLimit, greenTopLimit, Color.green);
            standarddialrange2.setInnerRadius(0.522D);
            standarddialrange2.setOuterRadius(0.554D);
            dialplot.addLayer(standarddialrange2);
            GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
            DialBackground dialbackground = new DialBackground(gradientpaint);
            dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
            dialplot.setBackground(dialbackground);
            dialplot.removePointer(0);
            org.jfree.chart.plot.dial.DialPointer.Pointer pointer = new org.jfree.chart.plot.dial.DialPointer.Pointer();
            dialplot.addPointer(pointer);
            add(new ChartPanel(jfreechart));
        }

        public void buildPlot2() {
            dataset = new DefaultValueDataset(0);
            DialPlot dialplot = new DialPlot();
            dialplot.setView(0.20D, 0.0D, 0.6D, 0.3D);
            dialplot.setDataset(dataset);
            ArcDialFrame arcdialframe = new ArcDialFrame(60D, 60D);
            arcdialframe.setInnerRadius(0.6D);
            arcdialframe.setOuterRadius(0.9D);
            arcdialframe.setForegroundPaint(Color.darkGray);
            arcdialframe.setStroke(new BasicStroke(3F));
            dialplot.setDialFrame(arcdialframe);
            GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(240, 240, 240));
            DialBackground dialbackground = new DialBackground(gradientpaint);
            dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
            dialplot.addLayer(dialbackground);
            StandardDialScale standarddialscale = new StandardDialScale(plotBottonLimit, plotTopLimit, 115D, -50D, majorDivisions, minorDivisions);
            standarddialscale.setTickRadius(0.88D);
            standarddialscale.setTickLabelOffset(0.07D);
            dialplot.addScale(0, standarddialscale);
            org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin();
            pin.setRadius(0.8D);
            dialplot.addLayer(pin);
            JFreeChart jfreechart = new JFreeChart(dialplot);
            jfreechart.setTitle(plotTitle);
            add(new ChartPanel(jfreechart));
        }

        public void buildPlot3() {
            dataset = new DefaultValueDataset(0);
            DialPlot dialplot = new DialPlot();
            dialplot.setView(0.78D, 0.37D, 0.22D, 0.26D);
            dialplot.setDataset(dataset);
            ArcDialFrame arcdialframe = new ArcDialFrame(-10D, 20D);
            arcdialframe.setInnerRadius(0.7D);
            arcdialframe.setOuterRadius(0.9D);
            arcdialframe.setForegroundPaint(Color.darkGray);
            arcdialframe.setStroke(new BasicStroke(3F));
            dialplot.setDialFrame(arcdialframe);
            GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(240, 240, 240));
            DialBackground dialbackground = new DialBackground(gradientpaint);
            dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
            dialplot.addLayer(dialbackground);
            StandardDialScale standarddialscale = new StandardDialScale(plotBottonLimit, plotTopLimit, -8D, 16D, majorDivisions, minorDivisions);
            standarddialscale.setTickRadius(0.8D);
            standarddialscale.setTickLabelOffset(-0.041D);
            standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
            dialplot.addScale(0, standarddialscale);
            org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin();
            pin.setRadius(0.84D);
            dialplot.addLayer(pin);
            JFreeChart jfreechart = new JFreeChart(dialplot);
            jfreechart.setTitle(plotTitle);
            add(new ChartPanel(jfreechart));
        }
    }

    private void build() {
        switch (PLOT_SELECTION) {
            case 1:
                PanamaHitek_SingleDialPlot.plot.buildPlot1();
                break;
            case 2:
                PanamaHitek_SingleDialPlot.plot.buildPlot2();
                break;
            case 3:
                PanamaHitek_SingleDialPlot.plot.buildPlot3();
                break;
        }
    }

    /**
     * Devuelve un JPanel con el grafico en su interior
     *
     * @return Panel con grafico incrustado
     */
    public JPanel getPlotPanel() {
        build();
        return PanamaHitek_SingleDialPlot.plot;
    }

    /**
     * Permite insertar le grafico generado en un JPanel dentro de cualquier
     * interfaz
     *
     * @param panel JPanel en el que se desea mostrar el grafico
     */
    public void insertToPanel(JPanel panel) {
        build();
        PanamaHitek_SingleDialPlot.plot.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        panel.add(plot);
    }

    /**
     * Configura el valor que se quiere mostrar en el grafico en un momento dado
     *
     * @param value Valor que se desea mostrar
     */
    public void setValue(double value) {
        PanamaHitek_SingleDialPlot.plot.getDataset().setValue(value);
    }

    /**
     * Configura el titulo del grafico
     *
     * @param title Titulo del grafico
     */
    public void setPlotTitle(String title) {
        PanamaHitek_SingleDialPlot.plot.setPlotTitle(title);
    }

    /**
     * Nombre de la variable a mostrar en el grafico
     *
     * @param variableName Nombre de la variable
     */
    public void setPlotVariableName(String variableName) {
        PanamaHitek_SingleDialPlot.plot.setVariableName(variableName);
    }

    /**
     * Valores limites de la grafica tipo reloj
     *
     * @param minValue Valor minimo
     * @param maxValue Valor maximo
     */
    public void setPlotLimitValues(int minValue, int maxValue) {
        PanamaHitek_SingleDialPlot.plot.setPlotBottonLimit(minValue);
        PanamaHitek_SingleDialPlot.plot.setPlotTopLimit(maxValue);
    }

    /**
     * Cantidad de divisiones (rayitas) mayores en el grafico
     *
     * @param majorDivisions Cantidad de divisiones
     */
    public void setPlotMajorDivisions(int majorDivisions) {
        PanamaHitek_SingleDialPlot.plot.setMajorDivisions(majorDivisions);
    }

    /**
     * Cantidad de divisiones (rayitas) menores en el grafico
     *
     * @param minorDivisions Cantidad de divisiones
     */
    public void setPlotMinorDivisions(int minorDivisions) {
        PanamaHitek_SingleDialPlot.plot.setMinorDivisions(minorDivisions);
    }

     /**
     * Distribucion de los colores segun porcentaje
     * @param firstArea Porcentaje del area verde
     * @param secondArea Porcentaje del area amarilla
     * @param thirdArea Porcentaje del area roja
     * @throws Exception Se dispara si la suma de los porcentajes no totaliza 100
     */
    public void setColorDistribuition(int firstArea, int secondArea, int thirdArea) throws Exception {
        int total = firstArea + secondArea + thirdArea;
        if (total != 100) {
            throw new Exception("La suma de los 3 porcentajes debe totalizar 100%");
        }
        PanamaHitek_SingleDialPlot.plot.setGreenSlice(firstArea * 0.01);
        PanamaHitek_SingleDialPlot.plot.setYellowSlice(secondArea * 0.01);
        PanamaHitek_SingleDialPlot.plot.setRedSlice(thirdArea * 0.01);
    }

    /**
     * Inicia el modo followUp de Arduino. En este modo se crea una conexion con
     * Arduino a traves del puerto serie. El Arduino debe estar configurado para
     * enviarle un dato numerico al programa en Java a traves de la funcion
     * Serial.println(). Se debe enviar un solo dato a la vez, siendo un valor
     * numerico sin caracteres especiales. Java lo recibira y lo traducira en
     * una posicion de la aguja en la grafica
     *
     * @param PORT_NAME Puerto COM en el que esta conectado Arduino
     * @param DATA_RATE Velocidad de transmision de datos. Debe ser la misma que
     * se configuro en el Arduino
     * @throws ArduinoException Posibles excepciones
     * @throws SerialPortException Posibles excepciones
     */
    public void createArduinoFollowUp(String PORT_NAME, int DATA_RATE) throws ArduinoException, SerialPortException {
        PanamaHitek_SingleDialPlot.ino = new PanamaHitek_Arduino();
        SerialPortEventListener listener;
        listener = (SerialPortEvent serialPortEvent) -> {
            try {
                if (ino.isMessageAvailable()) {
                    setValue(Double.parseDouble(ino.printMessage()));
                }
            } catch (SerialPortException | ArduinoException ex) {
                Logger.getLogger(PanamaHitek_SingleDialPlot.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        PanamaHitek_SingleDialPlot.ino.arduinoRX(PORT_NAME, DATA_RATE, listener);
    }

    /**
     * Detiene el modo followUp de Arduino, cerrando la conexion con el puerto
     * serie
     *
     * @throws ArduinoException default
     */
    public void stopArduinoFollowUp() throws ArduinoException {
        ino.killArduinoConnection();
    }

    /**
     * Constructor de la clase. Crea una grafica tipo reloj con nombre y titulos
     * por defecto
     *
     * @param plotType Tipo de grafico que se desea proyectar
     * <br>1 - Grafico circular
     * <br>2 - Grafico Horizontal
     * <br>3 - Grafico Vertical
     */
    public PanamaHitek_SingleDialPlot(int plotType) {
        PanamaHitek_SingleDialPlot.plot = new plotPanel();
        PanamaHitek_SingleDialPlot.PLOT_SELECTION = plotType;
    }

    /**
     * Constructor de la clase. Le asigna el valor del parametro plotTitle a la
     * grafica tipo reloj
     *
     * @param plotTitle Titulo de la grafica
     * @param plotType Tipo de grafico que se desea proyectar
     * <br>1 - Grafico circular
     * <br>2 - Grafico Horizontal
     * <br>3 - Grafico Vertical
     */
    public PanamaHitek_SingleDialPlot(int plotType, String plotTitle) {
        PanamaHitek_SingleDialPlot.plot = new plotPanel();
        PanamaHitek_SingleDialPlot.plot.setPlotTitle(plotTitle);
        PanamaHitek_SingleDialPlot.PLOT_SELECTION = plotType;
    }

    /**
     * Constructor de la clase. Le asigna titulo y nombre a la grafica tipo
     * reloj
     *
     * @param plotTitle Titulo de la grafica
     * @param variableName Nombre de la grafica
     * @param plotType Tipo de grafico que se desea proyectar
     * <br>1 - Grafico circular
     * <br>2 - Grafico Horizontal
     * <br>3 - Grafico Vertical
     */
    public PanamaHitek_SingleDialPlot(int plotType, String plotTitle, String variableName) {
        PanamaHitek_SingleDialPlot.plot = new plotPanel();
        PanamaHitek_SingleDialPlot.plot.setPlotTitle(plotTitle);
        PanamaHitek_SingleDialPlot.plot.setVariableName(variableName);
        PanamaHitek_SingleDialPlot.PLOT_SELECTION = plotType;
    }

}