/*
 * Created on Feb 25, 2005
 */
package comp557.a1.rotationControllers;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL2;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FlatMatrix4d;

/**
 * Implementation of a simple XY ball rotation interface
 * 
 * @author kry
 */
public class XYBall implements MouseListener, MouseMotionListener, RotationController {
    
	String name = "XYBall";
	
	@Override
	public String getName() {
		return name;
	}
	    
    /**
     * The mouse motion in pixels will be multiplied by this value before
     * applying a rotation in radians.  
     */
    private DoubleParameter gain = new DoubleParameter("gain", 0.01, 0.005, 0.1);
    
    /**
     * Our current transformation 
     */
    public Matrix4d bakedTransformation = new Matrix4d();
    
    /**
     * A flat matrix for passing to OpenGL, backed by our transformation matrix.
     * (see asArray and reconstitute methods of FlatMatrix)
     */
    public FlatMatrix4d transformation = new FlatMatrix4d(bakedTransformation);
            
    /**
     * Create a new XYBall with the default settings
     */
    public XYBall() {
        bakedTransformation.setIdentity();
    }
    
    /**
     * Attach this XYBall to the given component
     * @param component 
     */
    public void attach(Component component) {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }
    
    /** previous mouse position */
    Point previousPosition = new Point();
        
    public void mousePressed(MouseEvent e) {
    	previousPosition.setLocation( e.getPoint() );
    }
    
    public void mouseDragged(MouseEvent e) {
        // TODO: Objective 5: update the bakedTransformation to account for mouse movement
    	
        
                    
    	
    	previousPosition.setLocation( e.getPoint() );
    }

    public void mouseReleased(MouseEvent e) {
        // re normalize and orthgonalize
        Matrix3d m = new Matrix3d();        
        bakedTransformation.getRotationScale( m );
        m.normalizeCP();
        bakedTransformation.setRotationScale( m );
    }

    protected JPanel controlPanel;

    /**
     * Gets the controls for the XY ball
     * @return controls
     */
    public JPanel getControls() {
        if ( controlPanel != null ) return controlPanel;
        VerticalFlowPanel panel = new VerticalFlowPanel();
        panel.add( new JLabel("XYBall Settings") );
        panel.add(gain.getSliderControls(true));
        controlPanel = panel.getPanel();
        return controlPanel;        
    }

    public void mouseClicked(MouseEvent e) { /* do nothing */ }
    public void mouseEntered(MouseEvent e) { /* do nothing */ }
    public void mouseExited(MouseEvent e) { /* do nothing */ }
    public void mouseMoved(MouseEvent e) { /* do nothing */ }
            
    /**
     * Applies the transformation to the current matrix stack
     * @param gl
     */
    public void applyTransformation(GL2 gl) {
        gl.glMultMatrixd( transformation.asArray(), 0 );        
    }
 
}