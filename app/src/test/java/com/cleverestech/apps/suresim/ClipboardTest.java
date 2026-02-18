package com.cleverestech.apps.suresim;

import android.content.ClipData;
import android.content.ClipboardManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class ClipboardTest {

    @Test
    public void testCopyToClipboard_updatesWhenDifferent() {
        ClipboardManager clipboardManager = mock(ClipboardManager.class);
        String code = "12345";

        // Mock existing clip to be different or null
        when(clipboardManager.getPrimaryClip()).thenReturn(null);

        ModuleMain.copyToClipboard(clipboardManager, code);

        verify(clipboardManager, times(1)).setPrimaryClip(any(ClipData.class));
    }

    @Test
    public void testCopyToClipboard_skipsRedundantUpdate() {
        ClipboardManager clipboardManager = mock(ClipboardManager.class);
        String code = "SAME_CODE";

        // Mock existing clip with same content
        ClipData existingClip = ClipData.newPlainText("label", code);
        when(clipboardManager.getPrimaryClip()).thenReturn(existingClip);

        ModuleMain.copyToClipboard(clipboardManager, code);

        // Expectation: setPrimaryClip should NOT be called if optimization is in place
        verify(clipboardManager, never()).setPrimaryClip(any(ClipData.class));
    }
}
