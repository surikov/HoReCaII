package reactive.ui;


import android.content.*;
import android.view.*;
import android.view.View;
import android.widget.*;

public class GridScroll extends ScrollView {
	float initialX = -1000;
	float initialY = -1000;
	DataGrid grid;

	public GridScroll(Context c, DataGrid d) {
		super(c);
		this.grid = d;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (grid.columnsArray == null) {
			return false;
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			grid.pluckMode = false;
			if (grid.afterTap.property.value() != null) {
				grid.afterTap.property.value().start();
			}
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			grid.pluckMode = false;
			initialX = event.getX();
			initialY = event.getY();
		} else {
			if (grid.pluckMode) {
				//System.out.println("redirect "+event);
				grid.pluckX.is(event.getX());
				grid.pluckY.is(event.getY());
				return true;
			}
			float aY = event.getY();
			float aX = event.getX();
			int columnsWidth = 0;
			if (grid.columnsArray != null) {
				for (int x = 0; x < grid.columnsArray.length; x++) {
					columnsWidth = columnsWidth + grid.columnsArray[x].width.property.value().intValue();
				}
			}
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				//pluckMode = false;
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
				if (grid.afterPluck.property.value() != null) {
					//if (!pluckMode) {
					if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
						float delta = Math.abs(event.getX() - initialX);
						//pluckX.is(event.getX() - initialX);
						//pluckY.is(event.getY() - initialY);
						if (delta > Auxiliary.tapSize) {
							if (aX > grid.margin.value() && aX < grid.margin.value() + columnsWidth) {
								int nn = (int) ((this.getScrollY() + aY) / grid.rowHeight.property.value());
								grid.plucked = nn;
								//lastPluckMotionEvent = event;
								grid.pluckMode = true;
								grid.pluckX.is(event.getX());
								grid.pluckY.is(event.getY());
								grid.afterPluck.property.value().start();
								return true;
							}
						}
					}
					//}
					//else {
					//	return true;
					//}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
		//System.err.println("onScrollChanged "+grid.lockScroll+"/"+left+" / "+top+" / "+oldLeft+" / "+oldTop);
		super.onScrollChanged(left, top, oldLeft, oldTop);
		//System.err.println("lockScroll "+lockScroll+" for "+top);
		if (grid.lockScroll) {
			//if (progressBar.getVisibility() == View.VISIBLE) {
			//System.err.println("skip");
			return;
		} else {
			refreshScroll(top);
		}
	}
	@Override
	public void scrollTo(int x, int y) {
		//System.err.println("scrollTo "+x+"/"+y+"/"+getChildCount());
		super.scrollTo(x,y);

	}

	public void refreshScroll(int top) {
		//System.err.println("refreshScroll "+top);
		//grid.lastManualScrollY.property.value(top);
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
		double contentHeight = grid.rowHeight.property.value() * (grid.currentPage + 1) * grid.pageSize.property.value();
		//double contentHeight = rowHeight.property.value() * currentPage * pageSize.property.value();
		double limit = contentHeight - scrollViewHeight;
		//System.err.println(top + " / " + limit + " - " +  contentHeight+ " - " + scrollViewHeight);
		//System.out.println("refreshScroll " + top+" dataOffset "+grid.dataOffset.property.value()+" limit "+limit);
		if (top > 0 && limit > 0 && top >= limit) {
			//System.out.println("currentPage "+grid.currentPage);
			if (grid.currentPage < grid.maxPageCount - 1) {
				grid.currentPage++;
				grid.append();
				grid.progressBar.setVisibility(View.INVISIBLE);
				grid.lockScroll = false;
			} else {
				System.err.println("next");
				if (grid.beforeFlip.property.value() != null) {
					grid.flipNext();
				} else {
					grid.progressBar.setVisibility(View.INVISIBLE);
					grid.lockScroll = false;
				}
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
				//System.out.println("here");
				grid.progressBar.setVisibility(View.INVISIBLE);
				grid.lockScroll = false;
			}
		}
		//System.out.println("currentPage "+grid.currentPage);
	}
}
