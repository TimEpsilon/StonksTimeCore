#version 150

uniform sampler2D DiffuseSampler; // Texture input (contains the minecraft:main)
uniform float uDesaturate; // encodes the information "is player out?" from the CPU

in vec2 texCoord; // UV coordinate of the pixel
out vec4 fragColor; // output color

void main() {
    vec4 color = texture(DiffuseSampler, texCoord); // get texture color at pixel
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114)); // linear approximation of perceived brightness
    color.rgb = mix(color.rgb, vec3(gray), uDesaturate); // go from base color to black and white on a 0 - 1 scale
    fragColor = color;
}
