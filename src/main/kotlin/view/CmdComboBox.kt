package view

import bean.CmdContainer
import bean.ConstCmd
import interfces.*
import model.CmdModel
import org.slf4j.LoggerFactory
import java.awt.event.ActionListener
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.event.EventListenerList

class CmdComboBox : JComboBox<String>(), Observer<CmdContainer>, IView {
    private val logger = LoggerFactory.getLogger(FilterList::class.java)
    private val defaultMode = DefaultCmdModel()
    private var eventlisteners = EventListenerList()

    init {
        model = defaultMode
    }

    private var actionListener = ActionListener {
        val event = CustomEvent(this, ConstCmd.CMD_SELECT_RUN)
        for (listener in eventlisteners.getListeners(CustomActionListener::class.java)) {
            listener.actionPerformed(event)
        }
    }

    fun addCustomActionListener(l: CustomActionListener) {
        logger.debug("addCustomActionListener $l")
        eventlisteners.add(CustomActionListener::class.java, l)
    }

    fun removeCustomActionListener(l: CustomActionListener) {
        logger.debug("removeCustomActionListener $l")
        eventlisteners.remove(CustomActionListener::class.java, l)
    }

    override fun update(s: ObservableSubject<CmdContainer>) {
        if (s is CmdModel) {
            defaultMode.setData(s.getData())
            if (!s.getData().isEmpty()) {
                selectedIndex = 0
            }
        }
    }

    override fun initListener() {
        addActionListener(actionListener)
    }

    override fun deinitListenr() {
        removeActionListener(actionListener)
    }

    inner class DefaultCmdModel : DefaultComboBoxModel<String>() {
        private var arData = ArrayList<CmdContainer>()

        @Synchronized
        override fun getElementAt(p0: Int): String {
            return arData[p0].title
        }

        @Synchronized
        override fun getSize(): Int {
            return arData.size
        }

        @Synchronized
        fun setData(data: ArrayList<CmdContainer>) {
            arData.clear()
            arData.addAll(data)
            fireContentsChanged(this, 0, arData.size)
        }
    }
}