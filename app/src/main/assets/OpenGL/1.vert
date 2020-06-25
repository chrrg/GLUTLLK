precision mediump float;
attribute vec4 mPosition;
attribute vec2 mTexture;
uniform mat4 uMVPMatrix;
varying vec2 v_texCoord;
//attribute vec2 inputTextureCoordinate;
//attribute vec4 aColor;
//varying vec4 mColor;
//uniform mat4 transform;
//varying vec2 textureCoordinate;
void main(){
    //gl_Position 定点位置
//    gl_Position = transform*position;
//    mColor = aColor;
//    textureCoordinate = inputTextureCoordinate;
    v_texCoord = mTexture;
//    gl_Position=mPosition;
    gl_Position=mPosition * uMVPMatrix;
}