package sweetlife.android10.supervisor;

import android.os.*;
import android.app.*;
import android.content.*;

import reactive.ui.*;

import tee.binding.task.*;

public class ActivityWebView  extends Activity  {
    Layoutless layoutless;
    WebRender brwsr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutless = new Layoutless(this);
        setContentView(layoutless);
        int winW = Auxiliary.screenWidth(this);
        int winH = Auxiliary.screenWidth(this);
        brwsr = new WebRender(this).afterLink.is(new Task() {
            @Override
            public void doTask() {
                try {
                    final android.net.Uri uri = android.net.Uri.parse(brwsr.url.property.value());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(browserIntent);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        layoutless.child(brwsr//
                .width().is(winW)//
                .height().is(winH)//
        );
        String title=Auxiliary.activityExatras(this).child("title").value.property.value();
        this.setTitle(title);
        String startupURL=Auxiliary.activityExatras(this).child("startup").value.property.value();
        System.out.println("startupURL "+startupURL);
        brwsr.go(startupURL);
    }
}
