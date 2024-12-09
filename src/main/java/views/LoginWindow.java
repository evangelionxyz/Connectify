package views;

import core.AppManager;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import models.HRD;
import models.Mahasiswa;

public class LoginWindow extends WindowBase {

    private int loginTypeIndex;
    private String[] loginTypes;
    private String currentLoginType;

    private final ImString nameInput = new ImString(256);
    private final ImString usernameInput = new ImString(256);
    private final ImString passwordInput = new ImString(256);
    private final ImString companyInput = new ImString(256);

    private boolean showErrorMessage = false;
    private String errorMessage;

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

                final float windowWidth = ImGui.getWindowSize().x;
                final float textWidth = ImGui.calcTextSize("Login / Register Page").x;
                ImGui.setCursorPosX(windowWidth / 2.0f - textWidth / 2.0f);
                ImGui.text("Login / Register Page");

                ImGui.beginChild("##login_form", new ImVec2(0.0f, availableHeight), true);

                final float itemWidth = windowWidth / 3.0f;
                ImGui.pushItemWidth(itemWidth);

                ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                if (ImGui.combo(currentLoginType, imIntIndex, loginTypes)) {
                    loginTypeIndex = imIntIndex.get();
                    currentLoginType = loginTypes[loginTypeIndex];
                }

                ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                ImGui.inputText("Display Name", nameInput);
                ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                ImGui.inputText("Username", usernameInput);
                ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                ImGui.inputText("Password", passwordInput, ImGuiInputTextFlags.Password);
                // mahasiswa only

                ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                if (loginTypeIndex == 0) ImGui.inputText("University", companyInput);
                else if (loginTypeIndex == 1) ImGui.inputText("Company", companyInput);
                if (showErrorMessage) {
                    ImGui.setCursorPosX(windowWidth / 2.0f - itemWidth / 2.0f);
                    showErrorMessage();
                }

                ImGui.popItemWidth();
                ImGui.endChild();

                ImGui.beginChild("##login_buttons", new ImVec2(0.0f, 0.0f), true);

                Runnable loginUser = () -> {
                    showErrorMessage = false;
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
                    else {
                        showErrorMessage = true;
                        if (usernameInput.isEmpty()) {
                            errorMessage = "Username could not be empty";
                        } else if (passwordInput.isEmpty()) {
                            errorMessage = "Password could not be empty";
                        } else if (usernameInput.isEmpty() && passwordInput.isEmpty()) {
                            errorMessage = "Username and password could not be empty";
                        }else {
                            errorMessage = "Your username or password is incorrect";
                        }
                    }
                };

                ImGui.setCursorPosX(windowWidth / 2.0f - 210.0f);
                if (ImGui.button("Login", new ImVec2(200.0f, buttonHeight))) {
                    loginUser.run();
                }

                ImGui.sameLine();

                Runnable registerUser = () -> {
                    showErrorMessage = false;
                    if (nameInput.isNotEmpty() && usernameInput.isNotEmpty() && passwordInput.isNotEmpty() && companyInput.isNotEmpty()) {
                        if (loginTypeIndex == 0) {
                            AppManager.currentUser = new Mahasiswa(nameInput.get(), usernameInput.get(), companyInput.get());
                        }
                        else if (loginTypeIndex == 1) {
                            AppManager.currentUser = new HRD(nameInput.get(), usernameInput.get(), companyInput.get());
                        }
                        AppManager.currentUser.setPassword(passwordInput.get());
                    }

                    if (AppManager.currentUser != null) {
                        if (AppManager.getUserByUsername(usernameInput.get()) == null) {
                            if (AppManager.registerUser(AppManager.currentUser)) {
                                nameInput.clear();
                                passwordInput.clear();
                                companyInput.clear();
                                usernameInput.clear();
                                this.close();
                            }
                        } else {
                            showErrorMessage = true;
                            errorMessage = "Username is already taken";
                        }
                    } else {
                        showErrorMessage = true;
                        if (passwordInput.isEmpty()|| nameInput.isEmpty() || usernameInput.isEmpty() || companyInput.isEmpty()) {
                            errorMessage = "Please fill the forms to register";
                        }
                    }
                };

                ImGui.setCursorPosX(windowWidth / 2.0f + 10.0f);
                if (ImGui.button("Register", new ImVec2(200.0f, buttonHeight))) {
                    registerUser.run();
                }

                ImGui.endChild();

                ImGui.end();
            }
        }
    }

    private void showErrorMessage() {
        ImGui.textColored(new ImVec4(1.0f, 0.0f, 0.0f, 1.0f), errorMessage);
    }
}
