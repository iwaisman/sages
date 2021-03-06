package name.brijest.sages.gui


import scala.collection._
import scala.util.Sorting

import name.brijest.sages.model.Instance
import name.brijest.sages.model.objects.Town
import name.brijest.sages.model.ModelView
import name.brijest.sages.model.Event
import name.brijest.sages.model.AddInstanceEvent
import name.brijest.sages.model.RemoveInstanceEvent
import name.brijest.sages.model.InstancePropertyChangedEvent
import name.brijest.sages.controller._


trait TownClickListener {
  def onTownClick(hinst: Instance): Unit
}


/**
 * Concrete widgets should call:
 * Method <code>onCreate</code> once the widget is created and connected to the controller.
 * Method <code>onTownClick</code> whenever a town is clicked.
 * Method <code>player_=</code> to set which player's towns should be listed.
 * Method <code>objectdrawerpalette</code> to set the object drawer palette.
 * Concrete widgets should implement:
 * Method <code>refillTowns</code> which supplies the widget with the list of towns and their drawers.
 */
trait TownList extends Widget with EventListener {
  type TownInfo = (Instance, Drawer)
  
  private val listenerlist = mutable.Set[TownClickListener]()
  private val towns = mutable.Map[Int, Instance]()
  
  def refillTowns(hinstances: List[(Instance, Drawer)]): Unit
  
  var objectdrawerpalette: ObjectDrawerPalette = null
  var player = 0
  def onCreate {
    // find all towns
    controller view ((v: ModelView) => {
      for ((loc, inst) <- v.instanceMap) inst.prototype match {
        case t: Town if (t.owner == player) => towns.put(inst.index, inst)
        case _ =>
      }
    })
    invokeRefillTowns
  }
  def onTownClick(hinst: Instance) = for (hcl <- listenerlist) hcl onTownClick hinst
  def addTownClickListener(hcl: TownClickListener): Unit = listenerlist + hcl
  def removeTownClickListener(hcl: TownClickListener): Unit = listenerlist - hcl
  private def invokeRefillTowns {
    val arr = new Array[TownInfo](towns.size)
    var i = 0
    for ((ind, inst) <- towns) {
      arr(i) = (inst, objectdrawerpalette.createDrawer(inst.objectname, inst).helper("icon") match {
        case Some(d) => d
        case None => new EmptyDrawer
      })
      i = i + 1
    }
    Sorting.stableSort(arr, (f: TownInfo, s: TownInfo) => f._1.index < s._1.index)
    refillTowns(arr.toList)
  }
  def listens = List(classOf[AddInstanceEvent], classOf[RemoveInstanceEvent],
                     classOf[InstancePropertyChangedEvent])
  def onEvent(e: Event) = e match {
    case AddInstanceEvent(loc, inst) => 
      inst.prototype match {
        case h: Town if (h.owner == player) => towns.put(inst.index, inst)
        case _ =>
      }
      invokeRefillTowns
    case RemoveInstanceEvent(loc, inst) =>
      towns.removeKey(inst.index)
      invokeRefillTowns
    case InstancePropertyChangedEvent(inst, oldval, newval, pname, loc) if (pname == "Owner") =>
      towns.removeKey(inst.index)
      if (inst.prototype.owner == player) towns.put(inst.index, inst)
      invokeRefillTowns
    case _ =>
  }
}













