package reactive.ui;

import android.view.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.net.*;
import android.widget.*;
import android.widget.TextView.BufferType;
import java.util.*;
/*
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
*/
import android.database.*;
import android.database.sqlite.*;
import reactive.ui.*;
import java.net.*;
import java.nio.channels.FileChannel;
import android.view.animation.*;
import android.view.inputmethod.*;
import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
import java.io.*;
import java.text.*;

public abstract class Column {
	public NumericProperty<Column> width = new NumericProperty<Column>(this);
	public ToggleProperty<Column> noVerticalBorder = new ToggleProperty<Column>(this);
	public ToggleProperty<Column> noHorizontalBorder = new ToggleProperty<Column>(this);
	public abstract void afterRowsTap(int row);
	public NoteProperty<Column> title = new NoteProperty<Column>(this);
	public NoteProperty<Column> footer = new NoteProperty<Column>(this);
	public abstract Rake item(int column, int row, Context context);
	public abstract String export(int row);
	public abstract void update(int row);
	public abstract Rake header(Context context);
	public abstract int count();
	public abstract void clear();
	public abstract void highlight(int row);
	/*public int textAppearance = 2;
	public int textGravity = 2;
	public Column labelAlignLeftTop() {
		textGravity = 1;
		return this;
	}
	public Column labelAlignLeftCenter() {
		textGravity = 2;
		return this;
	}
	public Column labelAlignLeftBottom() {
		textGravity = 3;
		return this;
	}
	public Column labelAlignRightTop() {
		textGravity = 4;
		return this;
	}
	public Column labelAlignRightCenter() {
		textGravity = 5;
		return this;
	}
	public Column labelAlignRightBottom() {
		textGravity = 6;
		return this;
	}
	public Column labelAlignCenterTop() {
		textGravity = 7;
		return this;
	}
	public Column labelAlignCenterCenter() {
		textGravity = 8;
		return this;
	}
	public Column labelAlignCenterBottom() {
		textGravity = 9;
		return this;
	}
	public Column labelStyleSmallNormal() {
		textAppearance = 1;
		return this;
	}
	public Column labelStyleMediumNormal() {
		textAppearance = 2;
		return this;
	}
	public Column labelStyleLargeNormal() {
		textAppearance = 3;
		return this;
	}
	public Column labelStyleSmallInverse() {
		textAppearance = 4;
		return this;
	}
	public Column labelStyleMediumInverse() {
		textAppearance = 5;
		return this;
	}
	public Column labelStyleLargeInverse() {
		textAppearance = 6;
		return this;
	}
	public void setupCellStyleAlign(Decor cell) {
		switch (textAppearance) {
			case 1:
				cell.labelStyleSmallNormal();
				break;
			case 2:
				cell.labelStyleMediumNormal();
				break;
			case 3:
				cell.labelStyleLargeNormal();
				break;
			case 4:
				cell.labelStyleSmallInverse();
				break;
			case 5:
				cell.labelStyleMediumInverse();
				break;
			case 6:
				cell.labelStyleLargeInverse();
				break;
			default:
				//
				break;
		}
		switch (textGravity) {
			case 1:
				cell.labelAlignLeftTop();
				break;
			case 2:
				cell.labelAlignLeftCenter();
				break;
			case 3:
				cell.labelAlignLeftBottom();
				break;
			case 4:
				cell.labelAlignRightTop();
				break;
			case 5:
				cell.labelAlignRightCenter();
				break;
			case 6:
				cell.labelAlignRightBottom();
				break;
			case 7:
				cell.labelAlignCenterTop();
				break;
			case 8:
				cell.labelAlignCenterCenter();
				break;
			case 9:
				cell.labelAlignCenterBottom();
				break;
			default:
				//
				break;
		}
	}*/
}
