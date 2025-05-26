#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 originalColor = texture2D(u_texture, v_texCoords) * v_color;
    // Luminosity method for grayscale:
    float gray = dot(originalColor.rgb, vec3(0.299, 0.587, 0.114));
    gl_FragColor = vec4(gray, gray, gray, originalColor.a); // Keep original alpha
}

