package armada;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * @author Michael Kieburtz
 */
public class ArmadaWindow extends JFrame
{
    private final Panel panel;
    private final ArrayList<String> imagePaths = new ArrayList<>();
    private final ArrayList<BufferedImage> images = new ArrayList<>();
    private final BufferedImage[] initBorderFrames;
    private final BufferedImage[] horizontalBorderFrames;
    private final Renderer renderer;
    private Dimension windowSize;
    private final GameActionListener gameActionListener;
    private final MainMenu mainMenu;
    private final Dimension borderSize;
        
    public ArmadaWindow(GameActionListener actionListener)
    {
        // MOVE SOMEWHERE ELSE
        if (System.getProperty("os.name").contains("Mac"))
        {
            borderSize = new Dimension(getSize().width - getContentPane().getSize().width,
                getSize().height - getContentPane().getSize().height);
        }
        else
        {
            borderSize = new Dimension(getSize().width - getContentPane().getSize().width - 6,
                getSize().height - getContentPane().getSize().height - 8);
        }
        this.gameActionListener = actionListener;
        renderer = new Renderer(System.getProperty("os.name").contains("OS X"), new Dimension(1000, 600));
        mainMenu = new MainMenu(actionListener);
        imagePaths.add("Resources/SideBorder.png");
        imagePaths.add("Resources/TopBorder.png");
        imagePaths.add("Resources/InitBorder.png");
        imagePaths.add("Resources/InitVerticalBorder.png");
        
        images.addAll(GameData.getResources().getImagesForObject(imagePaths));
        DrawingData.setVerticalBorder(images.get(0));
        DrawingData.setHorizontalBorder(images.get(1));
        DrawingData.setInitBorder(images.get(2));
        initBorderFrames = GameData.getResources().getGeneratedImagesForObject(Resources.GeneratedImagesType.animatedInitBorder);
        horizontalBorderFrames = GameData.getResources().getGeneratedImagesForObject(Resources.GeneratedImagesType.animatedHoritontalBorder);
        DrawingData.setHorizontalBorderFrames(horizontalBorderFrames);
        DrawingData.setInitBorderFrames(initBorderFrames);
        panel = new Panel();
        setUpWindow();
        mainMenu.setButtonRects();
    }

    private void setUpWindow()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Armada");
        setMinimumSize(new Dimension(1000, 600));
        setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        
        windowSize = getSize();
        DrawingData.setScreenSize(windowSize);
        
        getContentPane().add(panel);
        
        addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent e) 
            {
                windowSize = getSize();
                DrawingData.setScreenSize(windowSize);
                mainMenu.setButtonRects();
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });
        
        setVisible(true);
        panel.initBufferStrategy();
    }
    
    public final class Panel extends JPanel
    {
        private BufferStrategy bufferStrategy;
        public Panel()
        {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setBackground(Color.BLACK);
            
            addMouseListener(new MouseAdapter() 
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    System.out.println("pressed");
                    mainMenu.checkMousePressed(e.getPoint());
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() 
            {
                @Override
                public void mouseMoved(MouseEvent e) 
                {
                    System.out.println(e.getLocationOnScreen());
                    mainMenu.checkMouseMoved(e.getLocationOnScreen());
                }
            });
        }
        
        public void initBufferStrategy()
        {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
        }

        public BufferStrategy getDrawingStrategy() 
        {
            return bufferStrategy;
        }
    }
    
    private Point convertToWindowCoords(Point screenCoords)
    {
        Point windowCoords = screenCoords;
        
    }
    
    private boolean doneWithInit = false;
    public void draw(GameState state)
    {
        
        switch (state)
        {
            case opening:
                if (renderer.drawInitBorders(panel.getDrawingStrategy()))
                {
                    gameActionListener.doneOpening();
                }
                break;
            case mainMenu:
                renderer.drawMainMenu(panel.getDrawingStrategy(), mainMenu);
                break;
            case playing:
                renderer.drawScreen(panel.getDrawingStrategy());
                break;
        }
    }
}
