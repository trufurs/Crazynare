package com.example.crazynare.gameofcards.Utils

import android.graphics.Camera
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlin.math.sin

class Flipping(private var fromView: View, private var toView: View, centerX: Int, centerY: Int) :
    Animation() {
    private var camera: Camera? = null

    private val centerX = centerX.toFloat()

    private val centerY = centerY.toFloat()

    private var forward = true

    private var visibilitySwapped = false

    /**
     * Creates a 3D flip animation between two views. If forward is true, its
     * assumed that view1 is "visible" and view2 is "gone" before the animation
     * starts. At the end of the animation, view1 will be "gone" and view2 will
     * be "visible". If forward is false, the reverse is assumed.
     *
     * @param fromView First view in the transition.
     * @param toView   Second view in the transition.
     * @param centerX  The center of the views in the x-axis.
     * @param centerY  The center of the views in the y-axis.
     */
    init {
        duration = 700
        fillAfter = true
        interpolator = AccelerateDecelerateInterpolator()
    }

    fun reverse() {
        forward = false
        val temp = toView
        toView = fromView
        fromView = temp
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        camera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        // Angle around the y-axis of the rotation at the given time. It is
        // calculated both in radians and in the equivalent degrees.
        val radians = Math.PI * interpolatedTime
        var degrees = (180.0 * radians / Math.PI).toFloat()

        // Once we reach the midpoint in the animation, we need to hide the
        // source view and show the destination view. We also need to change
        // the angle by 180 degrees so that the destination does not come in
        // flipped around. This is the main problem with SDK sample, it does not
        // do this.
        if (interpolatedTime >= 0.5f) {
            degrees -= 180f

            if (!visibilitySwapped) {
                fromView.visibility = View.GONE
                toView.visibility = View.VISIBLE

                visibilitySwapped = true
            }
        }

        if (forward) degrees = -degrees

        val matrix = t.matrix

        camera!!.save()
        camera!!.translate(0.0f, 0.0f, (150.0 * sin(radians)).toFloat())
        camera!!.rotateY(degrees)
        camera!!.getMatrix(matrix)
        camera!!.restore()

        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }
}
