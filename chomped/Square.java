package com.app.chomped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Square {
	private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +

	        "attribute vec4 vPosition;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = vPosition * uMVPMatrix;" +
	        "}";

	    private final String fragmentShaderCode =
	        "precision mediump float;" +
	        "uniform vec4 vColor;" +
	        "void main() {" +
	        "  gl_FragColor = vColor;" +
	        "}";

	    private final FloatBuffer vertexBuffer;
	    private final ShortBuffer drawListBuffer;
	    private final int program;
	    private int positionHandle;
	    private int colorHandle;
	    private int mMVPMatrixHandle;

	    // number of coordinates per vertex in this array
	    static final int COORDS_PER_VERTEX = 3;
	    static float squareCoords[] = { 0.05f,  -0.7f, 0.0f,   // top left
	                                    0.05f, -0.8f, 0.0f,   // bottom left
	                                    -0.05f, -0.8f, 0.0f,   // bottom right
	                                    -0.05f,  -0.7f, 0.0f }; // top right

	    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

	    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	    // Set color with red, green, blue and alpha (opacity) values
	    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

	    public Square() {
	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(
	        // (# of coordinate values * 4 bytes per float)
	                squareCoords.length * 4);
	        bb.order(ByteOrder.nativeOrder());
	        vertexBuffer = bb.asFloatBuffer();
	        vertexBuffer.put(squareCoords);
	        vertexBuffer.position(0);

	        // initialize byte buffer for the draw list
	        ByteBuffer dlb = ByteBuffer.allocateDirect(
	        // (# of coordinate values * 2 bytes per short)
	                drawOrder.length * 2);
	        dlb.order(ByteOrder.nativeOrder());
	        drawListBuffer = dlb.asShortBuffer();
	        drawListBuffer.put(drawOrder);
	        drawListBuffer.position(0);

	        // prepare shaders and OpenGL program
	        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
	                                                   vertexShaderCode);
	        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
	                                                     fragmentShaderCode);

	        program = GLES20.glCreateProgram();             // create empty OpenGL Program
	        GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
	        GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
	        GLES20.glLinkProgram(program);                  // create OpenGL program executables
	    }

	    public void draw(float[] mvpMatrix) {
	        // Add program to OpenGL environment
	        GLES20.glUseProgram(program);

	        // get handle to vertex shader's vPosition member
	        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");

	        // Enable a handle to the triangle vertices
	        GLES20.glEnableVertexAttribArray(positionHandle);

	        // Prepare the triangle coordinate data
	        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
	                                     GLES20.GL_FLOAT, false,
	                                     vertexStride, vertexBuffer);

	        // get handle to fragment shader's vColor member
	        colorHandle = GLES20.glGetUniformLocation(program, "vColor");

	        // Set color for drawing the triangle
	        GLES20.glUniform4fv(colorHandle, 1, color, 0);

	        // get handle to shape's transformation matrix
	        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
	        GLRenderer.checkGlError("glGetUniformLocation");

	        // Apply the projection and view transformation
	        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	        GLRenderer.checkGlError("glUniformMatrix4fv");

	        // Draw the square
	        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
	                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

	        // Disable vertex array
	        GLES20.glDisableVertexAttribArray(positionHandle);
	    }
}
