package bsu.rfct.course2.group8.Zhlobich;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private Double[][] originalData;
    private boolean showAxis = true;
    private boolean firstlunc = true;
    private boolean showMarkers = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private int selectedMarker;
    private double[][] viewport;
    private ArrayList<double[][]> undoHistory;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke selectionStroke;
    private Font labelsFont;
    private static DecimalFormat formatter;
    private boolean scaleMode;
    private boolean changeMode;
    private double[] originalPoint;
    private Rectangle2D.Double selectionRect;
    private Font axisFont;

    static {
        GraphicsDisplay.formatter = (DecimalFormat) NumberFormat.getInstance();
    }

    public GraphicsDisplay() {

        selectedMarker = -1;
        viewport = new double[2][2];
        undoHistory = new ArrayList<double[][]>(5);
        scaleMode = false;
        changeMode = false;
        originalPoint = new double[2];
        selectionRect = new Rectangle2D.Double();
        setBackground(Color.WHITE);
//        задаем пунктирную линию с значениями длина штриха, длина пробела
        BasicStroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.CAP_BUTT,
                1, new float[]{27,3, 9,3, 9,3, 27, 3, 3}, 0);
        graphicsStroke = dashed;
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        selectionStroke = new BasicStroke(1.0f, 0, 0, 10.0f,
                new float[]{10.0f, 10.0f}, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 20);
        Font areaFont = new Font("Serif", Font.BOLD, 16);
        labelsFont = new Font("Serif", 0, 10);
        GraphicsDisplay.formatter.setMaximumFractionDigits(5);
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
    }

    public void displayGraphics(Double[][] GraphicsData, Double[][] OriginalData) {
        this.graphicsData = new Double[GraphicsData.length][];
        originalData = new Double[OriginalData.length][];
        for (int i = 0; i < GraphicsData.length; i++) {
            double x = GraphicsData[i][0];
            double y = GraphicsData[i][1];
            this.graphicsData[i] = new Double[]{x, y};
            x = OriginalData[i][0];
            y = OriginalData[i][1];
            originalData[i] = new Double[]{x, y};
        }
        if (graphicsData == null || graphicsData.length == 0) return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }


        firstlunc = true;
        zoomToRegion(minX, maxY, maxX, minY);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        double scaleX = getSize().getWidth() / (viewport[1][0] - viewport[0][0]);
        ;
        double scaleY = getSize().getHeight() / (viewport[0][1] - viewport[1][1]);

        scale = Math.min(scaleX, scaleY);
        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY -
                    minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX -
                    minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        if (graphicsData == null || graphicsData.length == 0) return;

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);

        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
        paintLabels(canvas);
        paintSelection(canvas);

    }

    private void paintSelection(Graphics2D canvas) {
        if (!scaleMode) {
            return;
        }
        canvas.setStroke(selectionStroke);
        canvas.setColor(Color.BLACK);
        canvas.draw(selectionRect);
    }

    private void paintGraphics(Graphics2D canvas) {

        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        Double currentX = null;
        Double currentY = null;
        for (Double[] point : graphicsData) {
            if (point[0] >= viewport[0][0] && point[1] <= viewport[0][1] && point[0] <= viewport[1][0]) {
                if (point[1] < viewport[1][1]) {
                    continue;
                }
                if (currentX != null && currentY != null) {
                    canvas.draw(new Line2D.Double(xyToPoint(currentX, currentY), xyToPoint(point[0], point[1])));
                }
                currentX = point[0];
                currentY = point[1];
            }
        }
        if (firstlunc) {
            zoomToRegion(minX, maxY, maxX, minY);
            firstlunc = false;
        }
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        canvas.setStroke(new BasicStroke(1));
        canvas.setPaint(Color.BLACK);
        Ellipse2D.Double lastMarker = null;
        int i_n = -1;

        for (Double[] point : graphicsData) {
            ++i_n;
            if (point[0] >= viewport[0][0] && point[1] <= viewport[0][1] && point[0] <= viewport[1][0]) {
                if (point[1] < this.viewport[1][1]) {
                    continue;
                }
                Point2D.Double center = xyToPoint(point[0], point[1]);
                Point2D.Double corner = shiftPoint(center, 7, 7);
                boolean even = true;
                double mid=0;
                double num = Math.abs(point[1].doubleValue());
                for(double x=-15.0 ; x<mid ; x+=0.1) {
                	mid+=num;
                }
                while (num != 0) {
                    if (num>2*mid) {
                        even = false;
                        break;
                    }
                    num /= 10;
                }
                canvas.setColor(Color.BLACK);

                if (even) {
                    canvas.setColor(Color.GREEN);
                }

                Ellipse2D.Double marker = new Ellipse2D.Double();
                marker.setFrameFromCenter(center, corner);

                if (i_n == this.selectedMarker) {
                    lastMarker = marker;
                } else {
                    canvas.drawOval((int) (center.x), (int) (center.y), 1, 1);
                    canvas.drawLine((int) (center.x - 5.5), (int) (center.y-5.5), (int) (center.x + 5.5), (int) (center.y+5.5));
                    canvas.drawLine((int) (center.x - 5.5), (int) (center.y), (int) (center.x + 5.5), (int) (center.y));
                    canvas.drawLine((int) (center.x), (int) (center.y+5.5), (int) (center.x), (int) (center.y-5.5));
                    canvas.drawLine((int) (center.x+5.5), (int) (center.y - 5.5), (int) (center.x - 5.5), (int) (center.y + 5.5));
                }
            }
        }
        if (lastMarker != null) {
            canvas.setColor(Color.MAGENTA);
            canvas.setPaint(Color.MAGENTA);
            canvas.draw(lastMarker);
            canvas.fill(lastMarker);
        }
        canvas.setStroke(markerStroke);
    }

    private void paintLabels(final Graphics2D canvas) {
        canvas.setColor(Color.BLACK);
        canvas.setFont(labelsFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (selectedMarker >= 0) {
            Point2D.Double point = xyToPoint(graphicsData[selectedMarker][0], graphicsData[selectedMarker][1]);
            String label = "X=" + GraphicsDisplay.formatter.format(graphicsData[selectedMarker][0]) +
                    ", Y=" + GraphicsDisplay.formatter.format(graphicsData[selectedMarker][1]);
            Rectangle2D bounds = labelsFont.getStringBounds(label, context);
            canvas.setColor(Color.MAGENTA);
            canvas.drawString(label, (float) (point.getX() + 5.0), (float) (point.getY() - bounds.getHeight()));
        }
    }


    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minX <= 0.0 && maxX >= 0.0) {


            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                    xyToPoint(0, minY)));


            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);


            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5,
                    arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10,
                    arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);


            canvas.drawString("y", (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));

        }
        if (minY <= 0.0 && maxY >= 0.0) {

            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                    xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);

            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20,
                    arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);


            canvas.drawString("x", (float) (labelPos.getX() -
                    bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()) + 80);


        }
    }


    protected int findSelectedPoint(int x, int y) {
        if (graphicsData == null) {
            return -1;
        }
        int pos = 0;
        for (Double[] point : graphicsData) {
            Point2D.Double screenPoint = xyToPoint(point[0], point[1]);
            double distance = (screenPoint.getX() - x) * (screenPoint.getX() - x) + (screenPoint.getY() - y)
                    * (screenPoint.getY() - y);
            if (distance < 100.0) {
                return pos;
            }
            ++pos;
        }
        return -1;
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - this.viewport[0][0];
        double deltaY = this.viewport[0][1] - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected double[] translatePointToXY(int x, int y) {
        return new double[]{viewport[0][0] + x / scale, viewport[0][1] - y / scale};
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    public void zoomToRegion(double x1, double y1, double x2, double y2) {
        viewport[0][0] = x1;
        viewport[0][1] = y1;
        viewport[1][0] = x2;
        viewport[1][1] = y2;
        repaint();
    }


    public Double[][] getGraphicsData() {
        return graphicsData;
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }


    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void reset() {
        displayGraphics(originalData, originalData);
    }

    static void SetViewPort(GraphicsDisplay graphicsDisplay, double[][] viewport) {
        graphicsDisplay.viewport = viewport;
    }

    static void SetSelectionMarker(GraphicsDisplay graphicsDisplay, int selectedMarker) {
        graphicsDisplay.selectedMarker = selectedMarker;
    }

    static void SetOriginalPoint(GraphicsDisplay graphicsDisplay, double[] originalPoint) {
        graphicsDisplay.originalPoint = originalPoint;
    }

    static void SetChangeMode(final GraphicsDisplay graphicsDisplay, final boolean changeMode) {
        graphicsDisplay.changeMode = changeMode;
    }

    static void SetScaleMode(GraphicsDisplay graphicsDisplay, boolean scaleMode) {
        graphicsDisplay.scaleMode = scaleMode;
    }

    public class MouseHandler extends MouseAdapter implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent ev) {//клик
            if (ev.getButton() == 3) {
                if (GraphicsDisplay.this.undoHistory.size() > 0) {
                    GraphicsDisplay.SetViewPort(GraphicsDisplay.this,
                            GraphicsDisplay.this.undoHistory.get(GraphicsDisplay.this.undoHistory.size() - 1));
                    GraphicsDisplay.this.undoHistory.remove(GraphicsDisplay.this.undoHistory.size() - 1);
                } else {
                    GraphicsDisplay.this.zoomToRegion(GraphicsDisplay.this.minX, GraphicsDisplay.this.maxY,
                            GraphicsDisplay.this.maxX, GraphicsDisplay.this.minY);
                }
                GraphicsDisplay.this.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent ev) {//нажатие
            if (ev.getButton() != 1) {
                return;
            }
            GraphicsDisplay.SetSelectionMarker(GraphicsDisplay.this,
                    GraphicsDisplay.this.findSelectedPoint(ev.getX(), ev.getY()));
            GraphicsDisplay.SetOriginalPoint(GraphicsDisplay.this,
                    GraphicsDisplay.this.translatePointToXY(ev.getX(), ev.getY()));
            if (GraphicsDisplay.this.selectedMarker >= 0) {
                GraphicsDisplay.SetChangeMode(GraphicsDisplay.this, true);
                GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(8));
            } else {
                GraphicsDisplay.SetScaleMode(GraphicsDisplay.this, true);
                GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(5));
                GraphicsDisplay.this.selectionRect.setFrame(ev.getX(), ev.getY(), 1.0, 1.0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent ev) {//отпускание
            if (ev.getButton() != 1) {
                return;
            }
            GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(0));
            if (GraphicsDisplay.this.changeMode) {
                GraphicsDisplay.SetChangeMode(GraphicsDisplay.this, false);
            } else {
                GraphicsDisplay.SetScaleMode(GraphicsDisplay.this, false);
                double[] finalPoint = GraphicsDisplay.this.translatePointToXY(ev.getX(), ev.getY());
                GraphicsDisplay.this.undoHistory.add(GraphicsDisplay.this.viewport);
                GraphicsDisplay.SetViewPort(GraphicsDisplay.this, new double[2][2]);
                GraphicsDisplay.this.zoomToRegion(GraphicsDisplay.this.originalPoint[0], GraphicsDisplay.this.originalPoint[1],
                        finalPoint[0], finalPoint[1]);
                GraphicsDisplay.this.repaint();
            }
        }
    }

    public class MouseMotionHandler implements MouseMotionListener {
        @Override
        public void mouseMoved(MouseEvent ev) {//перемещение
            GraphicsDisplay.SetSelectionMarker(GraphicsDisplay.this,
                    GraphicsDisplay.this.findSelectedPoint(ev.getX(), ev.getY()));
            if (GraphicsDisplay.this.selectedMarker >= 0) {
                GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(8));
            } else {
                GraphicsDisplay.this.setCursor(Cursor.getPredefinedCursor(0));
            }
            GraphicsDisplay.this.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent ev) {//перетаскивание
            if (GraphicsDisplay.this.changeMode) {
                double[] currentPoint = GraphicsDisplay.this.translatePointToXY(ev.getX(), ev.getY());
                double newY = ((Double[]) GraphicsDisplay.this.graphicsData[GraphicsDisplay.this.selectedMarker])[1]
                        + (currentPoint[1] -
                        ((Double[]) GraphicsDisplay.this.graphicsData[GraphicsDisplay.this.selectedMarker])[1]);
                if (newY > GraphicsDisplay.this.viewport[0][1]) {
                    newY = GraphicsDisplay.this.viewport[0][1];
                }
                if (newY < GraphicsDisplay.this.viewport[1][1]) {
                    newY = GraphicsDisplay.this.viewport[1][1];
                }
                ((Double[]) GraphicsDisplay.this.graphicsData[GraphicsDisplay.this.selectedMarker])[1] = newY;
                GraphicsDisplay.this.repaint();
            } else {
                double width = ev.getX() - GraphicsDisplay.this.selectionRect.getX();
                if (width < 5.0) {
                    width = 5.0;
                }
                double height = ev.getY() - GraphicsDisplay.this.selectionRect.getY();
                if (height < 5.0) {
                    height = 5.0;
                }
                GraphicsDisplay.this.selectionRect.setFrame(GraphicsDisplay.this.selectionRect.getX(),
                        GraphicsDisplay.this.selectionRect.getY(), width, height);
                GraphicsDisplay.this.repaint();
            }
        }


    }
}