package cn.xeblog.plugin.game.dld.ui.login;

import com.intellij.vcs.log.ui.frame.MainFrame;
import jnr.ffi.annotations.In;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author eleven
 * @date 2023/10/24 14:43
 * @apiNote
 */
@Data
@Slf4j
public class LoginFormUi {
    private JPanel loginPanel;
    private JPanel comboBoxPanel;
    private JPanel typePanel;
    private JLabel type;
    private JComboBox<String> typeComboBox;
}
