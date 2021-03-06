package view

import bean.ConstCmd
import bean.FilterContainer
import interfces.CustomActionListener
import interfces.CustomEvent
import interfces.IView
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.EventListenerList

class FilterEditPanel : JPanel(), IView {

    private val logger = LoggerFactory.getLogger(FilterEditPanel::class.java)
    private var eventlisteners = EventListenerList()

    private val cbRegex = JComboBox<String>()
    private val tfText = JTextField()


    private val btnClean = JButton()
    private val btnOk = JButton()

    private var iRegex = 0
    private var strText = ""
    private var strUuid = ""
    private var bEnable = false
    private var newFilterInfo = true

    init {
        layout = BorderLayout()

        val jpEditPane = JPanel(GridLayout(3, 1))
        jpEditPane.border = BorderFactory.createTitledBorder("Filter Edit")

        val jpReg = JPanel(BorderLayout())
        val jlRegex = JLabel()
        jlRegex.text = "Regex:"
        jpReg.add(jlRegex, BorderLayout.WEST)
        cbRegex.addItem("Contains")
        cbRegex.addItem("Matches")
        jpReg.add(cbRegex, BorderLayout.CENTER)

        val jpText = JPanel(BorderLayout())
        val jlText = JLabel()
        jlText.text = "Text   :"
        jpText.add(jlText, BorderLayout.WEST)
        jpText.add(tfText, BorderLayout.CENTER)

        btnOk.text = " OK "
        btnOk.addActionListener {
            if (strText.isEmpty()) {
                return@addActionListener
            }
            if (newFilterInfo) {
                updateFilterData(formatNewFilterData(), ConstCmd.CMD_ADD_FILTER)
            } else {
                updateFilterData(formatFilterData(), ConstCmd.CMD_EDIT_FILTER_END)
            }
            newFilterInfo = true
        }

        btnClean.text = "Clean"
        btnClean.addActionListener {
            tfText.text = ""
            iRegex = 0
            bEnable = true
            newFilterInfo = true
        }

        val jpBtn = JPanel(BorderLayout())
        jpBtn.add(btnClean, BorderLayout.WEST)
        jpBtn.add(btnOk, BorderLayout.EAST)

        jpEditPane.add(jpReg)
        jpEditPane.add(jpText)
        jpEditPane.add(jpBtn)

        add(jpEditPane, BorderLayout.CENTER)
    }

    override fun initListener() {
        logger.debug("initListener")
        tfText.document.addDocumentListener(dlListener)

        cbRegex.addItemListener(itemListener)
    }

    override fun deinitListenr() {
        logger.debug("deinitListenr")
        tfText.document.removeDocumentListener(dlListener)

        cbRegex.removeItemListener(itemListener)
    }

    fun addCustomActionListener(l: CustomActionListener) {
        logger.debug("addCustomActionListener $l")
        eventlisteners.add(CustomActionListener::class.java, l)
    }

    fun removeCustomActionListener(l: CustomActionListener) {
        logger.debug("removeCustomActionListener $l")
        eventlisteners.remove(CustomActionListener::class.java, l)
    }

    private fun formatNewFilterData(): FilterContainer {
        val data = FilterContainer()
        data.regex = iRegex
        data.text = strText
        data.enabled = bEnable
        return data
    }

    private fun formatFilterData(): FilterContainer {
        val data = FilterContainer(strUuid)
        data.regex = iRegex
        data.text = strText
        data.enabled = bEnable
        return data
    }

    private fun updateFilterData(data: FilterContainer?, str: String) {
        val event = CustomEvent(this, str)
        event.objectValue = data
        for (listener in eventlisteners.getListeners(CustomActionListener::class.java)) {
            listener.actionPerformed(event)
        }
    }

    private var itemListener = ItemListener {
        if (it.stateChange != ItemEvent.SELECTED) return@ItemListener
        iRegex = cbRegex.selectedIndex
    }

    private var dlListener = object : DocumentListener {
        override fun changedUpdate(p0: DocumentEvent) {
            val text = p0.document.getText(0, p0.document.length)
            when (p0.document) {
                tfText.document -> {
                    strText = text
                }
            }
        }

        override fun insertUpdate(p0: DocumentEvent) {
            val text = p0.document.getText(0, p0.document.length)
            when (p0.document) {
                tfText.document -> {
                    strText = text
                }
            }
        }

        override fun removeUpdate(p0: DocumentEvent) {
            val text = p0.document.getText(0, p0.document.length)
            when (p0.document) {
                tfText.document -> {
                    strText = text
                }
            }
        }
    }

    fun editFilterInfo(filterInfo: FilterContainer) {
        cbRegex.selectedIndex = filterInfo.regex
        tfText.text = filterInfo.text
        strUuid = filterInfo.uuid
        bEnable = filterInfo.enabled
        newFilterInfo = false
    }

    fun cleanFilterInfo() {
        tfText.text = ""
        bEnable = true
        newFilterInfo = true
    }
}