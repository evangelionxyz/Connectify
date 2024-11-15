package views;

import core.AppManager;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import core.EncryptionUtils;
import models.HRD;
import models.Mahasiswa;

import javax.crypto.SecretKey;

public class LoginWindow extends WindowBase {

    private int loginTypeIndex;
    private String[] loginTypes;
    private String currentLoginType;

    private ImString nameInput = new ImString(256);
    private ImString usernameInput = new ImString(256);
    private ImString passwordInput = new ImString(256);
    private ImString companyInput = new ImString(256);

    private ImInt imIntIndex;

    @Override
    public void init() {
        loginTypes = new String[] {"Mahasiswa", "HRD"};
        loginTypeIndex = 0;
        currentLoginType = loginTypes[loginTypeIndex];
        imIntIndex = new ImInt(loginTypeIndex);
    }

    @Override
    public void render() {
        if (show.get())
        {
            int flags = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings;
            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getWorkPos());
            ImGui.setNextWindowSize(viewport.getWorkSize());

            final float buttonHeight = 25.0f;
            float availableHeight = ImGui.getContentRegionAvailY() - buttonHeight - 20;

            if (ImGui.begin("Login Window", show, flags)) {

                ImGui.text("Login / Register Page");

                ImGui.beginChild("##login_form", new ImVec2(0.0f, availableHeight), true);
                if (ImGui.combo(currentLoginType, imIntIndex, loginTypes)) {
                    loginTypeIndex = imIntIndex.get();
                    currentLoginType = loginTypes[loginTypeIndex];
                }

                ImGui.inputText("Display Name", nameInput);
                ImGui.inputText("Username", usernameInput);
                ImGui.inputText("Password", passwordInput, ImGuiInputTextFlags.Password);

                // mahasiswa only
                if (loginTypeIndex == 0) {
                    ImGui.inputText("University", companyInput);
                }
                else if (loginTypeIndex == 1) {
                    ImGui.inputText("Company", companyInput);
                }

                ImGui.endChild();

                ImGui.beginChild("##login_buttons", new ImVec2(0.0f, 0.0f), true);

                Runnable loginUser = () -> {
                    if (usernameInput.isNotEmpty() && passwordInput.isNotEmpty()) {
                        AppManager.currentUser = AppManager.loginUser(usernameInput.get(), passwordInput.get());
                    }

                    if (AppManager.currentUser != null) {
                        nameInput.clear();
                        passwordInput.clear();
                        companyInput.clear();
                        usernameInput.clear();
                        
                        this.close();
                    }
                };

                if (ImGui.button("Login", new ImVec2(200.0f, buttonHeight))) {
                    loginUser.run();
                }

                ImGui.sameLine();

                Runnable registerUser = () -> {
                    if (usernameInput.isNotEmpty() && passwordInput.isNotEmpty() && companyInput.isNotEmpty()) {
                        if (loginTypeIndex == 0) {
                            AppManager.currentUser = new Mahasiswa(nameInput.get(), usernameInput.get(), companyInput.get());
                        }
                        else if (loginTypeIndex == 1) {
                            AppManager.currentUser = new HRD(nameInput.get(), usernameInput.get(), companyInput.get());
                        }
                        AppManager.currentUser.setPassword(passwordInput.get());
                    }

                    if (AppManager.currentUser != null) {
                        nameInput.clear();
                        passwordInput.clear();
                        companyInput.clear();
                        usernameInput.clear();
                        if (AppManager.registerUser(AppManager.currentUser)) {
                            this.close();
                        }
                    }
                };

                if (ImGui.button("Register", new ImVec2(200.0f, buttonHeight))) {
                    registerUser.run();
                }

                ImGui.endChild();

                ImGui.end();
            }
        }
    }
}
