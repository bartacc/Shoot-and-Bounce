package com.artec.jumpgame.utils;

/**
 * Created by bartek on 09.08.16.
 */
public class GLScripts
{
    /*
    public final static String VERT =
            "attribute vec4 position;\n" +
            "attribute vec4 color;\n" +
            "attribute vec2 uv;\n" +
            " \n" +
            "varying vec2 uvVarying;\n" +
            "varying vec4 colorVarying;\n" +
            "varying vec2 posVarying;\n" +
            " \n" +
            "uniform mat4 u_projTrans;\n" +
            " \n" +
            " \n" +
            "void main()\n" +
            "{ \n" +
            "    gl_Position = u_projTrans * position;\n" +
            "    posVarying = position.xy;\n" +
            "    uvVarying = uv;\n" +
            "    colorVarying = color;\n" +
            "}\n";

    public final static String FRAG =

            "uniform sampler2D u_texture;\n" +
            " \n" +
            "varying vec2 uvVarying;\n" +
            "varying vec2 posVarying;\n" +
            " \n" +
            "uniform vec2 agk_resolution;\n" +
            " \n" +
            "uniform vec2 center; // Mouse position\n" +
            "uniform float time; // effect elapsed time\n" +
            "uniform vec3 shockParams; // 10.0, 0.8, 0.1\n" +
            "void main()\n" +
            "{\n" +
            "    vec2 uv = uvVarying.xy;\n" +
            "    vec2 texCoord = uv;\n" +
            "     \n" +
            "    float dist = distance(uv, center);\n" +
            "    if ((dist >= (time - shockParams.z)) && (dist <= (time + shockParams.z))) \n" +
            "    {\n" +
            "        float diff = (dist - time); \n" +
            "        float powDiff = 1.0 - pow(abs(diff*shockParams.x), shockParams.y); \n" +
            "        float diffTime = diff * powDiff; \n" +
            "        vec2 diffUV = normalize(uv - center); \n" +
            "        texCoord = uv + (diffUV * diffTime);\n" +
            "    }\n" +
            "    gl_FragColor = texture2D(u_texture, texCoord);\n" +
            "}";
            */

    public final static String VERT =
            "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "\n" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}";

    public final static String FRAG =
    "#ifdef GL_ES\n" +
            "    precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec2 center; // Mouse position\n" +
            "uniform float time; // effect elapsed time\n" +
            "uniform vec3 shockParams; // 10.0, 0.8, 0.1\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 uv = v_texCoords.xy;\n" +
            "    vec2 texCoord = uv;\n" +
            "\n" +
            "float distance = distance(uv, center);\n" +
            "if ( (distance <= (time + shockParams.z)) && \n" +
            "   (distance >= (time - shockParams.z)) ) \n" +
            " {\n" +
            " float diff = (distance - time); \n" +
            " float powDiff = 1.0 - pow(abs(diff*shockParams.x), \n" +
            "   \t                           shockParams.y); \n" +
            " float diffTime = diff  * powDiff; \n" +
            " vec2 diffUV = normalize(uv - center); \n" +
            " texCoord = uv + (diffUV * diffTime);\n" +
            " } \n" +
            "\n" +
            "   gl_FragColor = v_color * texture2D(u_texture, texCoord);\n" +
            "}\n";

    public final static String VERT_BASIC =
            "attribute vec4 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    v_color = a_color;\n" +
                    "    v_texCoords = a_texCoord0;\n" +
                    "    gl_Position = u_projTrans * a_position;\n" +
                    "}";

    public final static String FRAG_BASIC = "#ifdef GL_ES\n" +
            "    precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "void main(){\n" +
            "vec4 color = texture2D(u_texture, v_texCoords);\t\n" +
            "color.rgb = 1.0 - color.rgb;\n" +
            "gl_FragColor = color;\n" +
            "}";
}
