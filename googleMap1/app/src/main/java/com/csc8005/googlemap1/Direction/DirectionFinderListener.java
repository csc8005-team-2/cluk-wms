package com.csc8005.googlemap1.Direction;

import java.util.List;

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Routes> route);
}

