package name.brijest.sages.gui


import scala.collection._
import scala.util.Sorting

import name.brijest.sages.model.Instance
import name.brijest.sages.model.objects.Sage
import name.brijest.sages.model.ModelView
import name.brijest.sages.model.Event
import name.brijest.sages.model.AddInstanceEvent
import name.brijest.sages.model.RemoveInstanceEvent
import name.brijest.sages.model.InstancePropertyChangedEvent
import name.brijest.sages.controller._


trait SageClickListener {
  def onSageClick(hinst: Instance): Unit
}


/**
 * Concrete widgets should call:
 * Method <code>onCreate</code> once the widget is created and connected to the controller.
 * Method <code>onSageClick</code> whenever a sage is clicked.
 * Method <code>player_=</code> to set which player's sages should be listed.
 * Method <code>objectdrawerpalette</code> to set the object drawer palette.
 * Concrete widgets should implement:
 * Method <code>refillSagees</code> which supplies the widget with the list of sages and their drawers.
 */
trait SageList extends Widget with EventListener {
  type SageInfo = (Instance, Drawer)
  
  private val listenerlist = mutable.Set[SageClickListener]()
  private val sages = mutable.Map[Int, Instance]()
  
  def refillSages(hinstances: List[(Instance, Drawer)]): Unit
  
  var objectdrawerpalette: ObjectDrawerPalette = null
  var player = 0
  def onCreate {
    // find all sages
    controller view ((v: ModelView) => {
      for ((loc, inst) <- v.instanceMap) inst.prototype match {
        case h: Sage if (h.owner == player) => sages.put(inst.index, inst)
        case _ =>
      }
    })
    invokeRefillSages
  }
  def onSageClick(hinst: Instance) = for (hcl <- listenerlist) hcl onSageClick hinst
  def addSageClickListener(hcl: SageClickListener): Unit = listenerlist + hcl
  def removeSageClickListener(hcl: SageClickListener): Unit = listenerlist - hcl
  private def invokeRefillSages {
    val arr = new Array[SageInfo](sages.size)
    var i = 0
    for ((ind, inst) <- sages) {
      arr(i) = (inst, objectdrawerpalette.createDrawer(inst.objectname, inst).helper("icon") match {
        case Some(d) => d
        case None => new EmptyDrawer
      })
      i = i + 1
    }
    Sorting.stableSort(arr, (f: SageInfo, s: SageInfo) => f._1.index < s._1.index)
    refillSages(arr.toList)
  }
  def listens = List(classOf[AddInstanceEvent], classOf[RemoveInstanceEvent],
                     classOf[InstancePropertyChangedEvent])
  def onEvent(e: Event) = e match {
    case AddInstanceEvent(loc, inst) => 
      inst.prototype match {
        case h: Sage if (h.owner == player) => sages.put(inst.index, inst)
        case _ =>
      }
      invokeRefillSages
    case RemoveInstanceEvent(loc, inst) =>
      sages.removeKey(inst.index)
      invokeRefillSages
    case InstancePropertyChangedEvent(inst, oldval, newval, pname, loc) if (pname == "Owner") =>
      sages.removeKey(inst.index)
      if (inst.prototype.owner == player) sages.put(inst.index, inst)
      invokeRefillSages
    case _ =>
  }
}













