package com.github.timepsilon.items.client;

import com.github.timepsilon.items.custom.StonksTemporalChronoscopeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StonksTemporalChronoscopeItemRenderer extends GeoItemRenderer<StonksTemporalChronoscopeItem> {

    public StonksTemporalChronoscopeItemRenderer() {
        super(new StonksTemporalChronoscopeItemModel());
    }
}
