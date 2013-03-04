package com.app.chomped;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Triangle {
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
	    private final int program;
	    private int positionHandle;
	    private int colorHandle;
	    private int mMVPMatrixHandle;

	    static final int COORDS_PER_VERTEX = 3;
	    static float triangleCoords[] = { // in counterclockwise order:
	         0.0f,  -0.922008459f, 0.0f,   // top
	        -0.05f, -1f, 0.0f,   // bottom left
	         0.05f, -1f, 0.0f    // bottom right
	    };
	    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
	    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	    // Set color with red, green, blue and alpha (opacity) values
	    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	    public Triangle() {
	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(
	                // (number of coordinate values * 4 bytes per float)
	                triangleCoords.length * 4);
	        // use the device hardware's native byte order
	        bb.order(ByteOrder.nativeOrder());

	        // create a floating point buffer from the ByteBuffer
	        vertexBuffer = bb.asFloatBuffer();
	        // add the coordinates to the FloatBuffer
	        vertexBuffer.put(triangleCoords);
	        // set the buffer to read the first coordinate
	        vertexBuffer.position(0);

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

	        // Draw the triangle
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

	        // Disable vertex array
	        GLES20.glDisableVertexAttribArray(positionHandle);
	    }
}
