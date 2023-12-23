package com.rb.crafty.customViews

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rb.crafty.utils.Shapes

class MultiShapeLayout: FrameLayout {
    //Set the default shape type, path and paint variables.
    var shape = "Rectangle"
    lateinit var shapePath: Path
    lateinit var shapePaint: Paint
    lateinit var strokePaint: Paint

    //Set the stroke width, corners and stroke colour variables.
    var shapeStrokeWidth = 3
    var shapeColour: String = "#d1c4e9"
    var shapeStrokeColour = "#000000"
    var corners: Int = 20

    //Set the shapes class variable.
    lateinit var shapes: Shapes

    constructor(context: Context) : super(context){
        init(context, null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        //Load the shape.
        val loadedShape = getShapePath(shape, width.toFloat(), height.toFloat())


        //Reset the current path.
        shapePath.reset()
        //Set the current path to loaded path.
        shapePath.set(loadedShape)

        shapePath.fillType = Path.FillType.INVERSE_WINDING

        //Use the available attributes to draw the shape.
        canvas!!.drawPath(shapePath, shapePaint)
        shapePath.fillType = Path.FillType.WINDING
        canvas.drawPath(shapePath, strokePaint)



        //Set back the layer type.
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }

    fun init(context: Context, attrs: AttributeSet?) {
        setWillNotDraw(false)
        //Initialize the path and paint.
        shapePath = Path()

        shapePaint = Paint()
        shapePaint.style = Paint.Style.FILL
        shapePaint.isAntiAlias = true
        shapePaint.isDither = true
        shapePaint.color = Color.parseColor(shapeColour)

        strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
        strokePaint.color = Color.parseColor(shapeStrokeColour)
        strokePaint.strokeWidth = shapeStrokeWidth.toFloat()


        //Initialize shapes class.
        shapes = Shapes()

        //Set the layer to mask the view.
     /*   shapePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        setLayerType(LAYER_TYPE_SOFTWARE, null);*/

    //Only works for software layers.
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1){
            shapePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            setLayerType(LAYER_TYPE_SOFTWARE, shapePaint); //Only works for software layers
        } else {
                shapePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
        }
    }

    fun getShapePath(shape: String, width: Float, height: Float): Path {
        //Get the required path for the given shape.
        return when (shape) {
            shapes.CIRCLE_SHAPE -> shapes.circleShape(width, height, shapeStrokeWidth)
            shapes.RECT_SHAPE -> shapes.rectShape(width, height, shapeStrokeWidth)
            else -> Path()
        }
    }

    fun setCurrentShape(shape: String) {
        this.shape = shape

        //Update the view.
        postInvalidate()
    }

    fun setCurrentShapeColour(shapeColour: String) {
        this.shapeColour = shapeColour

        //Update the view.
        postInvalidate()
    }

    fun setStrokeWidth(shapeStrokeWidth: Int) {
        this.shapeStrokeWidth = shapeStrokeWidth

        //Update the view.
        postInvalidate()
    }

    fun setStrokeColour(shapeStrokeColour: String) {
        this.shapeStrokeColour = shapeStrokeColour

        //Update the view.
        postInvalidate()
    }

    fun setCornerRadius(corners: Int) {
        this.corners = corners

        //Update the view.
        postInvalidate()
    }
}