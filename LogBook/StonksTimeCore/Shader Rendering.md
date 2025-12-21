# Shader 
Minecraft uses openGL Shading Language (GLSL)

Every parallel process (*pipes*) for the GPU needs to be independent (they're *blind*). Each pipe is always busy, a thread can never know what it was doing before (*memoryless*)

## Basic Example

```GLSL
#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;

void main() {
	gl_FragColor = vec4(0.970,0.513,0.276,1.000);
}
```

A shader has a single `main` function that returns a color at the end : `gl_FragColor`
`gl_FragColor` is a reserved global variable

```GLSL
#ifdef GL_ES
precision mediump float;
#endif
```

This is a macro (uses a `#`) for pre compilation 
`#ifdef` : is defined
`ifndef` : is not defined

So this does,
If GL_ES is defined (like on mobile), precision of floats is set to medium (could be `lowp` or `highp`)

It is important to keep the `.` if it's a float, casting isn't automatic.
## Uniforms
Allows to send instructions from the CPU to the GPU (we say they're *uniform* to all threads, and they're read only)

They're defined on top of the code, right after the macros

| Var Type    | Notes |
| ----------- | ----- |
| float       |       |
| vec2        |       |
| vec3        |       |
| vec4        |       |
| mat2        |       |
| mat3        |       |
| mat4        |       |
| sampler2D   |       |
| samplerCube |       |


| Reserved Variables | Function                       |
| ------------------ | ------------------------------ |
| float u_time       | Time since load                |
| vec2 u_mouse       | Mouse position in screen pixel |
| vec2 u_resolution  | Canvas size                    |

## Pixel Dependent Rendering

`vec4 gl_FragColor` : default output
`vec4 gl_FragCoord` : default input, holds the screen coordinates of the pixel (*screen fragment*)
	This varies from one thread to another, we call it a *varying*

(0,0) is the bottom left

`step(threshold,value)` -> if `value < threshold`, return 0, if `value > threshold`, return 1
`smoothstep(p1,p2,value)` -> interpolates `value` between `p1` and `p2`

## Color

```GLSL
vec4 vector;
vector[0] = vector.r = vector.x = vector.s;
vector[1] = vector.g = vector.y = vector.t;
vector[2] = vector.b = vector.z = vector.p;
vector[3] = vector.a = vector.w = vector.q;
```

```GLSL
vec3 yellow, magenta, green;

// Making Yellow
yellow.rg = vec2(1.0);  // Assigning 1. to red and green channels
yellow[2] = 0.0;        // Assigning 0. to blue channel

// Making Magenta
magenta = yellow.rbg;   // Assign the channels with green and blue swapped

// Making Green
green.rgb = yellow.bgb; // Assign the blue channel of Yellow (0) to red and blue channels
```

`mix(colorA,colorB,percent)` interpolates between 2 colors

### Color Space
```GLSL
vec3 rgb2hsb( in vec3 c ){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz),
                 vec4(c.gb, K.xy),
                 step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r),
                 vec4(c.r, p.yzx),
                 step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)),
                d / (q.x + e),
                q.x);
}
```

HSL (Hue, Saturation, Brightness)


## Shapes
Distance Field : 
```GLSL
vec2 st = gl_FragCoord.xy/u_resolution;
 vec2 toCenter = vec2(0.500,0.480)-st;
```


# Shaders in the context of Minecraft

See [https://docs.google.com/document/d/15TOAOVLgSNEoHGzpNlkez5cryH3hFF3awXL5Py81EMk/edit?tab=t.0](https://docs.google.com/document/d/15TOAOVLgSNEoHGzpNlkez5cryH3hFF3awXL5Py81EMk/edit?tab=t.0)

We need :
- A post shader file (`shaders/post/<name>.json`) which references a fragment shader
- Fragment shader (`shaders/program/<name>.fsh`) that converts RGB to grayscale via luminance (*GLSL*)
	- Contains a `uDesaturate` that will give the information of if the shader should apply
- A Neoforge Client Hook

## Post Shader File

```json
{
  "targets": ["swap"],
  "passes": [
    {
      "name": "desaturate",
      "intarget": "minecraft:main",
      "outtarget": "swap",
      "uniforms": [
        { "name": "uDesaturate", "type": "float", "values": [0.0] }
      ]
    }
  ]
}
```

`targets` : declares temp frame buffers during post processing, holds string identifier that must then be referenced in `outtarget` or `intarget`

`passes` : an ordered list of rendering steps, execute top to bottom

`name` : the name of the shader in `shaders/program/<name>.fsh`

`intarget` : read source for the shader, `"minecraft:main"` yields the output from the minecraft screen

`outtarget` : write destination for the shader

`uniforms` : defines the GPU uniforms
