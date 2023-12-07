package reactive.ui;

import java.util.Vector;

import reactive.ui.*;

import android.content.*;
import android.graphics.*;

import tee.binding.properties.*;

import android.util.*;
import android.view.*;
import android.widget.*;

import tee.binding.task.*;
import tee.binding.it.*;

import android.text.*;
import android.text.style.UnderlineSpan;

public class HTMLBox extends TextView implements Rake{
	private int mode = Layoutless.NONE;
	private float startEventX = 0;
	private float startEventY = 0;
	private float initialShiftX = 0;
	private float initialShiftY = 0;
	//private float initialSpacing;
	//private float currentSpacing;
	//public NumericProperty<HTMLBox> dragX = new NumericProperty<HTMLBox>(this);
	//public NumericProperty<HTMLBox> dragY = new NumericProperty<HTMLBox>(this);
	public NoteProperty<HTMLBox> htmlText = new NoteProperty<HTMLBox>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	//public NumericProperty<HTMLBox> labelColor = new NumericProperty<HTMLBox>(this);
	//public NumericProperty<HTMLBox> background = new NumericProperty<HTMLBox>(this);
	//public ItProperty<HTMLBox, Typeface> labelFace = new ItProperty<HTMLBox, Typeface>(this); // .face.is(Typeface.createFromAsset(me.getAssets(), "fonts/PoiretOne-Regular.ttf"))
	//public NumericProperty<HTMLBox> labelSize = new NumericProperty<HTMLBox>(this);
	//public ToggleProperty<HTMLBox> movableX = new ToggleProperty<HTMLBox>(this);
	//public ToggleProperty<HTMLBox> movableY = new ToggleProperty<HTMLBox>(this);
	//public ItProperty<HTMLBox, Bitmap> bitmap = new ItProperty<HTMLBox, Bitmap>(this);//Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rocket),200,100,true);
	//public ItProperty<HTMLBox, Task> afterTap = new ItProperty<HTMLBox, Task>(this);
	//public ItProperty<HTMLBox, Task> afterDrag = new ItProperty<HTMLBox, Task>(this);
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	//public Vector<Sketch> sketches = new Vector<Sketch>();
	//Context context;
	//Paint paint = new Paint();
	boolean initialized = false;
	private boolean inTableRow = false;


	Task reFit = new Task(){
		@Override
		public void doTask(){
			//System.out.println("reFit: " + width.property.value()+" * "+height.property.value());
			//if(1==1)return;
			//RelativeLayout.LayoutParams
			//TableRow.LayoutParams
			//System.out.println("reFit: "+inTableRow);
			ViewGroup.MarginLayoutParams params;
			if(inTableRow){
				params = new
						//RelativeLayout.LayoutParams
						TableRow.LayoutParams(//
						width.property.value().intValue()//
						, height.property.value().intValue());
			}else{
				params = new RelativeLayout.LayoutParams
						//		TableRow.LayoutParams
						(//
								width.property.value().intValue()//
								, height.property.value().intValue());
			}
			params.leftMargin = left.property.value().intValue();
			params.topMargin = top.property.value().intValue();
			//params.leftMargin = (int)(left.property.value() + dragX.property.value());
			//params.topMargin = (int)(top.property.value() + dragY.property.value());
			HTMLBox.this.setLayoutParams(params);
			HTMLBox.this.setWidth(width.property.value().intValue());
			HTMLBox.this.setHeight(height.property.value().intValue());
			HTMLBox.this.setMinWidth(width.property.value().intValue());
			HTMLBox.this.setMinHeight(height.property.value().intValue());
			HTMLBox.this.setMaxWidth(width.property.value().intValue());
			HTMLBox.this.setMaxHeight(height.property.value().intValue());
		}
	};
	Task postInvalidate = new Task(){
		@Override
		public void doTask(){
			postInvalidate();
		}
	};
/*
	public HTMLBox labelAlignLeftTop(){
		setGravity(android.view.Gravity.LEFT | android.view.Gravity.TOP);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignLeftCenter(){
		setGravity(android.view.Gravity.LEFT | android.view.Gravity.CENTER_VERTICAL);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignLeftBottom(){
		setGravity(android.view.Gravity.LEFT | android.view.Gravity.BOTTOM);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignRightTop(){
		setGravity(android.view.Gravity.RIGHT | android.view.Gravity.TOP);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignRightCenter(){
		setGravity(android.view.Gravity.RIGHT | android.view.Gravity.CENTER_VERTICAL);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignRightBottom(){
		setGravity(android.view.Gravity.RIGHT | android.view.Gravity.BOTTOM);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignCenterTop(){
		setGravity(android.view.Gravity.CENTER_HORIZONTAL | android.view.Gravity.TOP);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignCenterCenter(){
		setGravity(android.view.Gravity.CENTER_HORIZONTAL | android.view.Gravity.CENTER_VERTICAL);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelAlignCenterBottom(){
		setGravity(android.view.Gravity.CENTER_HORIZONTAL | android.view.Gravity.BOTTOM);
		setText(labelText.property.value(), BufferType.SPANNABLE);
		return this;
	}

	public HTMLBox labelStyleSmallNormal(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
		return this;
	}

	public HTMLBox labelStyleMediumNormal(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Medium);
		return this;
	}

	public HTMLBox labelStyleLargeNormal(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large);
		return this;
	}

	public HTMLBox labelStyleSmallInverse(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small_Inverse);
		return this;
	}

	public HTMLBox labelStyleMediumInverse(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Medium_Inverse);
		return this;
	}

	public HTMLBox labelStyleLargeInverse(){
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large_Inverse);
		return this;
	}
*/
	public HTMLBox(Context context, boolean tableRowMode){
		super(context);
		this.inTableRow = tableRowMode;
		init();
	}

	public HTMLBox(Context context){
		super(context);
		init();
	}

	public HTMLBox(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}

	public HTMLBox(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init();
	}

	void init(){
		if(initialized){
			return;
		}
		initialized = true;
		//this.context = c;
		//movableX.is(false);
		//movableY.is(false);
		//paint.setColor(0xff000000);
		//paint.setAntiAlias(true);
		//paint.setFilterBitmap(true);
		//paint.setDither(true);
		htmlText.property.afterChange(new Task(){
			@Override
			public void doTask(){
				//setText(htmlText.property.value(), BufferType.SPANNABLE);
				Spanned spanned=Html.fromHtml( htmlText.property.value() );
				setText(spanned, BufferType.SPANNABLE);
				/*
				SpannableString content = new SpannableString(labelText.property.value());
				content.setSpan(new UnderlineSpan(), 0, labelText.property.value().length(), 0);
				setText(content);
				*/
			}
		});
		//this.setTextAppearance(this.getContext(), android.R.style.textapp.TextAppearance_Large);
		/*labelColor.is(this.getCurrentTextColor());
		labelColor.property.afterChange(new Task(){
			@Override
			public void doTask(){
				int clr = labelColor.property.value().intValue();
				//clr=Color.parseColor("#bd5522");
				//System.out.println("set color "+clr);
				HTMLBox.this.setTextColor(clr);
			}
		});*/
		/*background.property.afterChange(new Task(){
			@Override
			public void doTask(){
				//System.out.println("set bgcolor "+background.property.value().intValue());
				setBackgroundColor(background.property.value().intValue());
			}
		});
		bitmap.property.afterChange(new Task(){
			@Override
			public void doTask(){
				invalidate();
			}
		});*/
		/*labelFace.property.afterChange(new Task(){
			@Override
			public void doTask(){
				if(labelFace.property.value() != null){
					setTypeface(labelFace.property.value());
				}
			}
		});*/
		/*labelSize.is(this.getTextSize());
		labelSize.property.afterChange(new Task(){
			@Override
			public void doTask(){
				if(labelSize.property.value().floatValue() != getTextSize()){
					setTextSize(labelSize.property.value().floatValue());
				}
			}
		});*/
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		//dragY.property.afterChange(reFit);
		//dragX.property.afterChange(reFit);
		hidden.property.afterChange(new Task(){
			@Override
			public void doTask(){
				if(hidden.property.value()){
					setVisibility(View.INVISIBLE);
				}else{
					setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/*
		public HTMLBox sketch(Sketch f) {
			this.sketches.add(f);
			f.forUpdate = this;
			this.postInvalidate();
			return this;
		}

		public void drop(Sketch f) {
			this.sketches.remove(f);
			f.forUpdate = null;
			this.postInvalidate();
		}
	*/
	public void clear(){
		/*for (int i = 0; i < sketches.size(); i++) {
			this.sketches.get(i).forUpdate = null;
			this.sketches.get(i).unbind();
		}
		this.sketches.removeAllElements();*/
		this.postInvalidate();
	}
/*
	void setShift(float x, float y){
		if(movableX.property.value()){
			double newShiftX = dragX.property.value() + x - startEventX;
			//double newShiftY = shiftY.property.value() + y - startEventY;
			dragX.property.value(newShiftX);
			//shiftY.property.value(newShiftY);
		}
		if(movableY.property.value()){
			//double newShiftX = shiftX.property.value() + x - startEventX;
			double newShiftY = dragY.property.value() + y - startEventY;
			//shiftX.property.value(newShiftX);
			dragY.property.value(newShiftY);
		}
	}*/
/*
	void finishDrag(float x, float y){
		setShift(x, y);
		if(Math.abs(initialShiftX - dragX.property.value()) < 1 + 0.7 * Auxiliary.tapSize//
				&& Math.abs(initialShiftY - dragY.property.value()) < 1 + 0.1 * Auxiliary.tapSize){
			finishTap(x, y);
		}else{
			if(movableX.property.value() || movableY.property.value()){
				if(afterDrag.property.value() != null){
					afterDrag.property.value().start();
				}
			}
		}
		mode = Layoutless.NONE;
	}

	void finishTap(float x, float y){
		dragX.property.value((double)initialShiftX);
		dragY.property.value((double)initialShiftY);
		if(afterTap.property.value() != null){
			afterTap.property.value().start();
		}
	}
*/
/*
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(!(afterTap.property.value() != null || movableX.property.value() || movableY.property.value())){
			return false;
		}
		if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN //
				&& (afterTap.property.value() != null //
				|| movableX.property.value() //
				|| movableY.property.value()//
		)){
			initialShiftX = dragX.property.value().floatValue();
			initialShiftY = dragY.property.value().floatValue();
			startEventX = event.getX();
			startEventY = event.getY();
			mode = Layoutless.DRAG;
			//this.bringToFront();
		}else{
			if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE //
					&& (afterTap.property.value() != null //
					|| movableX.property.value()//
					|| movableY.property.value()//
			)){
				setShift(event.getX(), event.getY());
			}else{
				if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
					if(mode == Layoutless.DRAG){
						finishDrag(event.getX(), event.getY());
					}else{
						//
					}
				}else{
					//
				}
			}
		}
		return true;
	}
*/
	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		/*if(bitmap.property.value() != null){
			canvas.drawBitmap(bitmap.property.value(), 0, 0, paint);
		}*/
		/*for (int i = 0; i < sketches.size(); i++) {
			sketches.get(i).draw(canvas);
		}*/
	}

	@Override
	public NumericProperty<Rake> left(){
		return left;
	}

	@Override
	public NumericProperty<Rake> top(){
		return top;
	}

	@Override
	public NumericProperty<Rake> width(){
		return width;
	}

	@Override
	public ToggleProperty<Rake> hidden(){
		return hidden;
	}

	@Override
	public NumericProperty<Rake> height(){
		return height;
	}

	@Override
	public View view(){
		return this;
	}

	@Override
	protected void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		//System.out.println(this.getClass().getCanonicalName()+".onDetachedFromWindow");
		//dragX.property.unbind();
		//dragY.property.unbind();
		htmlText.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
		//labelColor.property.unbind();
		//background.property.unbind();
		//labelFace.property.unbind();
		//labelSize.property.unbind();
		//movableX.property.unbind();
		//movableY.property.unbind();
		//bitmap.property.unbind();
		//afterTap.property.unbind();
		//afterDrag.property.unbind();
		clear();
	}
}
