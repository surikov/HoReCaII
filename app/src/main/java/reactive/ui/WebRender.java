package reactive.ui;

import tee.binding.properties.*;
import tee.binding.task.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class WebRender extends WebView implements Rake {
	boolean initialized = false;
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public NoteProperty<WebRender> url = new NoteProperty<WebRender>(this);
	public ItProperty<WebRender, Task> afterLink = new ItProperty<WebRender, Task>(this);
	//public ItProperty<WebRender, Task> afterLoad = new ItProperty<WebRender, Task>(this);
	//public NoteProperty<WebView> active = new NoteProperty<WebView>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	int maxH = 0;
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	//private boolean shouldOverrideUrlFlag = true;

	public static int browserScrollY = 0;

	Task reFit;


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//System.out.println(this.getClass().getCanonicalName() + ".onSizeChanged "+w+"/"+ h+" <- "+oldw+"/"+ oldh);
		super.onSizeChanged(w, h, oldw, oldh);
		//System.out.println(this.getClass().getCanonicalName() + ".onSizeChanged done");
	}

	public WebRender(Context context) {
		super(context);
		init();
	}

	public WebRender(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WebRender(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		if (initialized) {
			return;
		}
		getSettings().setJavaScriptEnabled(true);
		getSettings().setSaveFormData(true);
		getSettings().setBuiltInZoomControls(true);
		width.property.value(100);
		height.property.value(100);
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String urlString) {
				//System.out.println("WebRender.shouldOverrideUrlFlag " + shouldOverrideUrlFlag + " for " + urlString);

				//if (shouldOverrideUrlFlag) {
				url.is(urlString);
				if (afterLink.property.value() != null) {
					System.out.println("WebRender.afterLink start");
					afterLink.property.value().start();
				}
				return true;
				//} else {
				//	return false;
				//}

			}

			@Override
			public void onPageFinished(WebView view, String urlString) {
				System.out.println("WebRender.onPageFinished scroll" + urlString);
				new android.os.Handler().postDelayed(new Runnable() {
					public void run() {
						scrollBack();
					}
				}, 1999);
			}

			void scrollBack() {
				//shouldOverrideUrlFlag = true;
				setEnabled(true);

				System.out.println("now scrollY " + browserScrollY);
				if (browserScrollY > 0) {

					scrollTo(0, browserScrollY);
				}
				browserScrollY = 0;
			}

		});
		hidden.property.afterChange(new Task() {
			@Override
			public void doTask() {
				if (hidden.property.value()) {
					setVisibility(View.INVISIBLE);
				} else {
					setVisibility(View.VISIBLE);
				}
			}
		});
		initialized = true;
	}

	public void go(String urlString) {
		System.out.println("WebRender.go " + urlString);
		//shouldOverrideUrlFlag = false;
		this.setEnabled(false);
		loadUrl(urlString);
	}

	public String getQueryParameter(String name) {
		android.net.Uri uri = android.net.Uri.parse(this.url.property.value());
		//System.out.println(path);
		//System.out.println(uri);
		//System.out.println(name);
		try {
			String txtval = uri.getQueryParameter(name).trim();
			return txtval;
		} catch (Throwable t) {
			t.printStackTrace();
			return "";
		}
	}

	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
	}

	@Override
	public NumericProperty<Rake> left() {
		return left;
	}

	@Override
	public NumericProperty<Rake> top() {
		return top;
	}

	@Override
	public NumericProperty<Rake> width() {
		return width;
	}

	@Override
	public NumericProperty<Rake> height() {
		return height;
	}

	@Override
	public View view() {
		return this;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		reFit = new Task() {
			@Override
			public void doTask() {
				if (maxH >= height.property.value().intValue()) {//hack for 01-03 11:04:39.918: W/webcore(1539): skip viewSizeChanged as w is 0
					return;
				}
				maxH = height.property.value().intValue();
				int ww = width.property.value().intValue();
				if (ww <= 0) {
					ww = 300;
				}
				int hh = height.property.value().intValue();
				if (hh <= 0) {
					hh = 200;
				}
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ww, hh);
				params.leftMargin = left.property.value().intValue();
				params.topMargin = top.property.value().intValue();
				setLayoutParams(params);
			}
		};
		width.property.afterChange(reFit);
		height.property.afterChange(reFit);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
	}
}
