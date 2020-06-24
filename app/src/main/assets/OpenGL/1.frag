#extension GL_OES_EGL_image_external : require
precision mediump float;
//varying vec2 textureCoordinate;
//uniform sampler2D vTexture;
//varying vec4 mColor;
varying vec2 v_texCoord;
//uniform sampler2D vTexture;
uniform samplerExternalOES vTexture;
void main(){
    //gl_FragColor 片元颜色
//    gl_FragColor = mix (texture2D(vTexture,vec2(1.0-textureCoordinate.x,textureCoordinate.y)) , mColor,0.2);
    gl_FragColor = texture2D(vTexture,v_texCoord);
}