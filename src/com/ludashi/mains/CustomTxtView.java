package com.ludashi.mains;
import android.content.Context;   
import android.text.Layout;   
import android.text.Selection;   
import android.util.AttributeSet;
import android.view.ContextMenu;   
import android.view.MenuItem;
import android.view.MotionEvent;   
import android.widget.EditText;
import android.widget.TextView;
  
public class CustomTxtView extends TextView {   

	private final static int C_MENU_BEGIN_SELECTION = 0;
    boolean bIsBeginSelecting = false;
    int line = 0;	// 光标所在行
    int off = 0;	// 光标所在列
    
    private class MenuHandler implements MenuItem.OnMenuItemClickListener {
        public boolean onMenuItemClick(MenuItem item) {
            return onContextMenuItem(item.getItemId());
        }
    }
    
    public boolean onContextMenuItem(int id) {
    	switch (id) {
    	case C_MENU_BEGIN_SELECTION:
    		bIsBeginSelecting = true;
    		setCursorVisible(true);
    		return true;
    	}
    	
		return false;
    }
    
    public CustomTxtView(Context context, AttributeSet attrs) {
        super(context, attrs); //, 16842884
    }
    
    public CustomTxtView(Context context) {   
        super(context);   
    }    
       
//    // 长按屏幕弹出的上下文菜单
//    @Override  
//    protected void onCreateContextMenu(ContextMenu menu) {   
//    		MenuHandler handler = new MenuHandler();
//    		menu.add(0, C_MENU_BEGIN_SELECTION, 0, "文本选择模式").
//            setOnMenuItemClickListener(handler);
//    }   
       
    @Override  
    public boolean getDefaultEditable() {   
        return false;   
    }   
       
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
    	if (bIsBeginSelecting) {
    		// 文本选择模式下特殊处理
            int action = event.getAction();   
            Layout layout = getLayout();   

            switch(action) {   
            case MotionEvent.ACTION_DOWN:   
                line = layout.getLineForVertical(getScrollY()+ (int)event.getY());           
                off = layout.getOffsetForHorizontal(line, (int)event.getX());   
                Selection.setSelection(getEditableText(), off);   
                break;   
            case MotionEvent.ACTION_MOVE:   
            case MotionEvent.ACTION_UP:   
                line = layout.getLineForVertical(getScrollY()+(int)event.getY());    
                int curOff = layout.getOffsetForHorizontal(line, (int)event.getX());
                if (curOff > off)
                	Selection.setSelection(getEditableText(), off, curOff);
                else
                	Selection.setSelection(getEditableText(), curOff, off);
            }   
            return true;  
    	} else {
    		super.onTouchEvent(event);
    		return true;
    	}
    }   
    
    // 清除选择内容
    public void clearSelection() {
    	Selection.removeSelection(getEditableText());
		bIsBeginSelecting = false;
		setCursorVisible(false);
    }
    
    public boolean isInSelectMode() {
    	return bIsBeginSelecting;
    }
}  