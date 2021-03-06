package comp557.a1.rotationControllers;

import java.awt.Component;

import javax.media.opengl.GL2;
import javax.swing.JPanel;


import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

public class XYXRotationControls implements RotationController {
   
	String name = "XYX";
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void attach(Component component) {
		// do nothing, mouse interaction unused!		
	}
	
	DoubleParameter X = new DoubleParameter( "X", 0, -180, 180 );
	DoubleParameter Y = new DoubleParameter( "Y", 0, -180, 180 );
	DoubleParameter X2 = new DoubleParameter( "X", 0, -180, 180 );

	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
    	DoubleParameter.DEFAULT_SLIDER_LABEL_WIDTH = 30;
    	DoubleParameter.DEFAULT_SLIDER_TEXT_WIDTH = 80;
    	vfp.add( X.getSliderControls(false) );
    	vfp.add( Y.getSliderControls(false) );
    	vfp.add( X2.getSliderControls(false) );
    	return vfp.getPanel();
	}
	
	public void applyTransformation( GL2 gl ) {
		gl.glRotated(X2.getValue(), 1, 0, 0);
		gl.glRotated(Y.getValue(), 0, 1, 0);
		gl.glRotated(X.getValue(), 1, 0, 0);
	}

}
