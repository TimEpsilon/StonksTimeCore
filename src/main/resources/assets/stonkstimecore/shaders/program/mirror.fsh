#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    uv.x = 1.0 - uv.x;
    vec4 InTexel = texture(DiffuseSampler, uv);

    fragColor = vec4(InTexel.rgb, 1.0);
}
