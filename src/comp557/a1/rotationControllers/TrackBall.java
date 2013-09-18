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
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FlatMatrix4d;

/**
 * Implementation of a simple track ball
 * 
 * @author kry
 */
public class TrackBall implements MouseListener, MouseMotionListener, RotationController {
    
	String name = "TrackBall";
	
	@Override
	public String getName() {
		return name;
	}
	    
    /**
     * The computed angle of rotation will be multiplied by this value before
     * applying the rotation.  Values larger than one are useful for getting
     * useful amounts of rotation without requiring lots of mouse movement.
     */
    private DoubleParameter trackballGain = new DoubleParameter("gain", 1.2, 0.1, 5);
    
    /**
     * The fit parameter describes how big the ball is relative to the smallest 
     * screen dimension.  With a square window and fit of 2, the ball will fit
     * just touch the edges of the screen.  Values less than 2 will give a ball
     * larger than the window while smaller values will give a ball contained 
     * entirely inside. 
     */
    private DoubleParameter trackballFit = new DoubleParameter("fit", 2.0, 0.1, 5);
      
    /** The component (canvas) on which we are receiving mouse motion events */
    private Component trackingSource;
    
    /** 
     * previous track ball vector 
     */
    private Vector3d tbv0 = new Vector3d();

    /** 
     * current track ball vector 
     */
    private Vector3d tbv1 = new Vector3d();
            
    /**
     * Our current transformation 
     */
    public Matrix4d bakedTransformation = new Matrix4d();
    
    /**
     * A flat matrix for passing to opengl, backed by our transformation matrix
     */
    public FlatMatrix4d transformation = new FlatMatrix4d(bakedTransformation);
            
    /**
     * Create a new track ball with the default settings
     */
    public TrackBall() {
        bakedTransformation.setIdentity();
    }
    
    /**
     * Attach this track ball to the given component.
     * @param component 
     */
    public void attach(Component component) {
    	trackingSource = component;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }
    
    /**
     * Set track ball vector v, given the mouse position and the window size.
     * @param point
     * @param v
     * @param vnp
     */
    private void setTrackballVector( Point point, Vector3d v ) { 
        int width = trackingSource.getWidth();
        int height = trackingSource.getHeight();

    	// TODO: Objective 6: Implement the TrackBall rotation
        
    	double smallestDimension = (width < height ? width : height);
    	double r = trackballFit.getValue() * smallestDimension / 4;
    	
    	
    	double x = point.getX() - (width / 2);
    	double y = -(point.getY() - (height / 2));
    	double z = 0;
    	
    	if((x*x) + (y*y) < (r*r))
    		z = Math.sqrt((r*r) - (x*x) - (y*y));
        
    	v.set(x,y,z);
    	v.normalize();
    }    

    public void mousePressed(MouseEvent e) {
        setTrackballVector( e.getPoint(), tbv1 );
        tbv0.set( tbv1 );
    }
    
    public void mouseDragged(MouseEvent e) {
        setTrackballVector( e.getPoint(), tbv1 );
          
    	// TODO: Objective 6: Implement the TrackBall rotation
        Vector3d axis = new Vector3d();
        
        axis.cross(tbv0, tbv1);
        
        double angle = tbv0.dot(tbv1) / (tbv1.length() * tbv0.length());
        angle = Math.acos(angle) * trackballGain.getValue();
        
        AxisAngle4d axisAngle = new AxisAngle4d();
        axisAngle.set(axis, angle);
        
        Matrix4d transform = new Matrix4d();
        transform.set(axisAngle);
        transform.mul(bakedTransformation);
        
        bakedTransformation.set(transform);

        // copy current vector to previous 
        tbv0.set( tbv1 );
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
     * Gets the controls for the track ball 
     * @return controls
     */
    public JPanel getControls() {
        if ( controlPanel != null ) return controlPanel;
        VerticalFlowPanel panel = new VerticalFlowPanel();
        panel.add( new JLabel("TrackBall Settings") );
        panel.add(trackballFit.getSliderControls(false));
        panel.add(trackballGain.getSliderControls(true));
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