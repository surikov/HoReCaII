package reactive.ui;

import android.content.*;
import android.view.*;
import android.view.View;
import android.widget.*;

public class GridScroll2 extends ScrollView {
	float initialX = -1000;
	float initialY = -1000;
	DataGrid2 grid;

	public GridScroll2(Context c, DataGrid2 d) {
		super(c);
		this.grid = d;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (grid.columnsArray == null) {
			return false;
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			if (grid.afterTap.property.value() != null) {
				grid.afterTap.property.value().start();
			}
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			initialX = event.getX();
			initialY = event.getY();
		} else {
			float aY = event.getY();
			float aX = event.getX();
			int columnsWidth = 0;
			if (grid.columnsArray != null) {
				for (int x = 0; x < grid.columnsArray.length; x++) {
					columnsWidth = columnsWidth + grid.columnsArray[x].width.property.value().intValue();
				}
			}
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				if (aX > grid.margin.value() && aX < grid.margin.value() + columnsWidth) {
					float diff = 4;
					if (Math.abs(initialX - aX) < diff && Math.abs(initialY - aY) < diff) {
						int nn = (int) ((this.getScrollY() + aY) / grid.rowHeight.property.value());
						double xx = 0;
						for (int i = 0; i < grid.columnsArray.length; i++) {
							xx = xx + grid.columnsArray[i].width.property.value();
							if (xx > aX - grid.margin.value()) {
								grid.tapColumnRow(nn, i);
								break;
							}
						}
					}
				}
			} else {
				//
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
		super.onScrollChanged(left, top, oldLeft, oldTop);
		if (grid.lockScroll) {
			return;
		} else {
			refreshScroll(top);
		}
	}

	/*@Override
	public void scrollTo(int x, int y) {
		//System.err.println("scrollTo "+x+"/"+y+"/"+getChildCount());
		super.scrollTo(x, y);

	}*/

	public void refreshScroll(int top) {
		//System.out.println("refreshScroll "+top);
		grid.progressBar.setVisibility(View.VISIBLE);
		grid.lockScroll = true;
		grid.progressBar.postInvalidate();
		double scrollViewHeight = grid.height().property.value();
		if (!grid.noHead.property.value()) {
			scrollViewHeight = scrollViewHeight - grid.headerHeight.property.value();
		}
		if (!grid.noFoot.property.value()) {
			scrollViewHeight = scrollViewHeight - grid.footerHeight.property.value();
		}
		double contentHeight = grid.rowHeight.property.value() * grid.pageSize.property.value();
		double limit = contentHeight - scrollViewHeight;
		if (top > 0 && limit > 0 && top >= limit) {
			System.err.println("next");
			if (grid.beforeFlip.property.value() != null) {
				grid.flipNext();
			} else {
				grid.progressBar.setVisibility(View.INVISIBLE);
				grid.lockScroll = false;
			}
		} else {
			if (top <= 0) {
				if (grid.dataOffset.property.value() > 0) {
					if (grid.beforeFlip.property.value() != null) {
						grid.flipPrev();
					} else {
						grid.progressBar.setVisibility(View.INVISIBLE);
						grid.lockScroll = false;
					}
				} else {
					grid.progressBar.setVisibility(View.INVISIBLE);
					grid.lockScroll = false;
				}
			} else {
				grid.progressBar.setVisibility(View.INVISIBLE);
				grid.lockScroll = false;
			}
		}
	}
}
