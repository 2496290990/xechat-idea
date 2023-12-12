package cn.xeblog.plugin.game.dld.ui.login;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

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
    private JRadioButton localRadio;
}
