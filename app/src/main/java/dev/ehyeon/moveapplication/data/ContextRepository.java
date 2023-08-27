package dev.ehyeon.moveapplication.data;

import android.content.Context;

// Repository injected with context
public interface ContextRepository {

    void initializeContext(Context context);
}
