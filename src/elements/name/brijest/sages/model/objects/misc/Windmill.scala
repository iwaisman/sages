package name.brijest.sages.model.objects.misc


import name.brijest.sages.model.objects.NonListening
import name.brijest.sages.model.Object
import name.brijest.sages.gui.AnimatedObjectDrawer
import name.brijest.sages.model.Event
import name.brijest.sages.model.objects.ObjectImageCacher


class Windmill extends Object with NonListening {
  def objectname = "Windmill"
  def nicename = "Windmill"
  def size = (1, 1)
  def interactive = List((0, 0))
  def interact(thisloc: (Int, Int), that: Object, thatloc: (Int, Int)) = null
}

class WindmillDrawer
extends AnimatedObjectDrawer(-24, -35, ObjectImageCacher.loopedAni(classOf[Windmill], "windmill.png", 2)) {
  def name = "Windmill"
  def boundingbox = ((-24, -35), (24, 14))
  def copy = new WindmillDrawer
  def effectDrawerFor(e: Event) = None
}





