package net.hawthorn.dndsheets.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RollScrollWidget extends AbstractScrollWidget {

    private static class ListItem {
        public ListItem(EditBox name, List<Button> rollButtons, List<Button> editButtons, Button deleteButton) {
            this.nameBox = name;
            this.rollButtons = rollButtons;
            this.editButtons = editButtons;
            this.deleteButton = deleteButton;
        }
        public EditBox nameBox;
        public List<Button> rollButtons;
        public List<Button> editButtons;
        public Button deleteButton;
        public String getName() {
            return nameBox.getValue();
        }
    }

    private final List<ListItem> list = new ArrayList<>();
    private boolean editMode = false;

    public int separation = 20;
    public int scrollCutoff = 8;

    public RollScrollWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            this.renderBackground(guiGraphics);
            guiGraphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
            guiGraphics.pose().pushPose();
            //guiGraphics.pose().translate(0.0D, -this.scrollAmount, 0.0D);
            this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.pose().popPose();
            guiGraphics.disableScissor();
            this.renderDecorations(guiGraphics);
        }
    }

    @Override
    protected int getInnerHeight() {
        int innerHeight = this.getHeight();
        if (list.size() >= scrollCutoff) {
            int add = list.size() - scrollCutoff;
            innerHeight += add * separation + 7;
        }
        return innerHeight;
    }

    /**
     * <p>Using this method will put the ImageButtons and EditBox under the control of the scroll widget. This means their positioning, active state and visibility are driven by it.</p>
     * <p>Functionally the things here are grouped together as a singular new "list item" with the items being visually sorted alphabetically (using the contents of the EditBox). The buttons themselves are put next to each other.</p>
     * <p>It's very important each thing here is added to the screen with the screen's addWidget() method first, or it won't work.</p>
     * @param nameBox
     * @param rollButtons
     * @param editButtons
     */
    public void addListItem(EditBox nameBox, List<Button> rollButtons, List<Button> editButtons, Button deleteButton) {
        list.add(new ListItem(nameBox, rollButtons, editButtons, deleteButton));
    }

    public int getListSize() {
        return list.size();
    }

    /**
     * <p>This is asking for the delete button so it knows which list item to target.</p>
     * <p>This releases control over the widgets in the list item. If you want them to disappear, make sure you use the screen's removeWidget() method.</p>
     * @param button
     */
    public int removeListItem(Button button) {
        int toRemove = 0;
        for (int i = 0; i < list.size(); i++) {
            ListItem item = list.get(i);
            if (item.deleteButton == button) {
                toRemove = i;
            }
        }
        list.remove(toRemove);
        if (list.size() < scrollCutoff) {
            this.setScrollAmount(0);
        }
        return toRemove;
    }

    public void setActive(boolean newState) {
        active = newState;
        visible = newState;
        list.forEach((item) -> {
            item.rollButtons.forEach((button) -> {
                button.active = newState;
                button.visible = newState;
            });
            item.editButtons.forEach((button) -> {
                button.active = newState;
                button.visible = newState;
            });
            item.nameBox.active = newState;
            item.nameBox.visible = newState;
            item.deleteButton.active = newState;
            item.deleteButton.visible = newState;
        });
    }

    /**
     * <p>This sets the internal boolean for whether edit buttons or roll buttons are visible on this widget.</p>
     */
    public void setEditMode(boolean isEditMode) {
        this.editMode = isEditMode;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
        //This is meant to cancel click events so they go to buttons instead.
    }

    @Override
    protected double scrollRate() {
        if (list.size() >= scrollCutoff) return 8;
        else return 0;

    }

    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    public String[] getNames() {
        List<String> names = new ArrayList<>();
        String[] arr = new String[0];
        list.forEach((item) -> {
            names.add(item.getName());
        });
        return names.toArray(arr);
    }

    public EditBox[] getEditBoxes() {
        List<EditBox> editboxes = new ArrayList<>();
        EditBox[] arr = new EditBox[0];
        list.forEach((item) -> {
            editboxes.add(item.nameBox);
        });
        return editboxes.toArray(arr);
    }

    /**
     * <p>This tries to get the index of this button somewhere in the list, if it exists.</p>
     * @param button
     * @return
     */
    public int getIndex(Button button) {
        for (int i = 0; i < list.size(); i++) {
            ListItem item = list.get(i);
            for (int j = 0; j < item.rollButtons.size(); j++) {
                Button btnItem = item.rollButtons.get(j);
                if (button == btnItem) {
                    return i;
                }
            }
            for (int j = 0; j < item.editButtons.size(); j++) {
                Button btnItem = item.editButtons.get(j);
                if (button == btnItem) {
                    return i;
                }
            }
        }
        return 0;
    }


    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < list.size(); i++) {
            ListItem item = list.get(i);
            boolean isActive;

            //Set delete button position
            item.deleteButton.setX(this.getX() + 4);
            item.deleteButton.setY(this.getY() + separation*(i) + 9 - (int)this.scrollAmount());
            isActive = (item.deleteButton.getY() >= this.getY() - 8) && (item.deleteButton.getY() <= this.getY() + 8 + this.getHeight()) && editMode;
            if (isActive) item.deleteButton.render(guiGraphics, mouseX, mouseY, partialTicks);
            item.deleteButton.active = isActive;
            item.deleteButton.visible = isActive;

            //Set roll button positions
            for (int j = 0; j < item.rollButtons.size(); j++) {
                Button button = item.rollButtons.get(j);
                button.setX(this.getX() + this.getWidth() - 20*(j) - 20);
                button.setY(this.getY() + separation*(i) - (int)this.scrollAmount() + 4);
                isActive = (button.getY() >= this.getY() - 16) && (button.getY() <= this.getY() + 16 + this.getHeight()) && !editMode;
                if (isActive) button.render(guiGraphics, mouseX, mouseY, partialTicks);
                button.active = isActive;
                button.visible = isActive;
            }
            //Repeat for edit buttons
            for (int j = 0; j < item.editButtons.size(); j++) {
                Button button = item.editButtons.get(j);
                button.setX(this.getX() + this.getWidth() - 20*(j) - 20);
                button.setY(this.getY() + separation*(i) - (int)this.scrollAmount() + 4);
                isActive = (button.getY() >= this.getY() - 16) && (button.getY() <= this.getY() + 16 + this.getHeight()) && editMode;
                if (isActive) button.render(guiGraphics, mouseX, mouseY, partialTicks);
                button.active = isActive;
                button.visible = isActive;
            }

            //Set namebox position
            item.nameBox.setX(this.getX() + 16);
            item.nameBox.setY(this.getY() + separation*(i) - (int)this.scrollAmount() + 4);
            isActive = (item.nameBox.getY() >= this.getY() - 16) && (item.nameBox.getY() <= this.getY() + 16 + this.getHeight());
            if (isActive) item.nameBox.render(guiGraphics, mouseX, mouseY, partialTicks);
            item.nameBox.active = isActive;
            item.nameBox.visible = isActive;
        }

    }

}
