package net.hawthorn.dndsheets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.impl.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RollIndex {

    private static final Logger log = LogManager.getLogger(DndsheetsMod.MODID);

    public enum Category {
        CHECKS(0) {
            @Override
            public String toString() { return "checks"; }
        },
        SAVES(1) {
            @Override
            public String toString() { return "saves"; }
        },
        SKILLS(2) {
            @Override
            public String toString() { return "skills"; }
        },
        ATTACKS(3) {
            @Override
            public boolean isAdvanced() {return true;}
            @Override
            public String toString() { return "attacks"; }
        };

        public boolean isAdvanced() {return false;}

        private final int catNum;
        Category (int catNum) {
            this.catNum = catNum;
        }
        public int getInt() {
            return catNum;
        }
        public static Category fromInt(int value) {
            return switch(value) {
                case 0 -> Category.CHECKS;
                case 1 -> Category.SAVES;
                case 2 -> Category.SKILLS;
                case 3 -> Category.ATTACKS;
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        }
    }
    private final Category category;
    private final int index;
    private final int subIndex;

    public RollIndex(int category, int index, int subIndex) {
        this.category = Category.fromInt(category);
        this.index = index;
        this.subIndex = subIndex;
    }

    public RollIndex(int category, int index) {
        this.category = Category.fromInt(category);
        this.index = index;
        this.subIndex = 0;
    }

    public RollIndex(Category category, int index, int subIndex) {
        this.category = category;
        this.index = index;
        this.subIndex = subIndex;
    }

    public RollIndex(Category category, int index) {
        this.category = category;
        this.index = index;
        this.subIndex = 0;
    }

    public Category getCategory() {
        return category;
    }
    public int getIndex() {
        return index;
    }
    public int getSubIndex() {
        return subIndex;
    }

    public List<String> findContextsInSheet(JsonObject sheet) {

        JsonArray arr = sheet.getAsJsonArray(category.toString());
        List<String> output = new ArrayList<>();

        if (category.isAdvanced()) {
            JsonObject rollForm = arr.get(index).getAsJsonObject();
            JsonArray rollSet = rollForm.getAsJsonArray("rolls");
            JsonArray rollGroup = rollSet.get(subIndex).getAsJsonArray();

            rollGroup.forEach((item) -> {
                JsonObject roll = item.getAsJsonObject();
                output.add(roll.get("context").getAsString());
            });
        }
        else {

            output.add(getBasicContext());
            log.info(output);
        }

        return output;
    }

    public List<String> findExpressionsInSheet(JsonObject sheet) {

        JsonArray arr = sheet.getAsJsonArray(category.toString());
        List<String> output = new ArrayList<>();

        if (category.isAdvanced()) {
            JsonObject rollForm = arr.get(index).getAsJsonObject();
            JsonArray rollSet = rollForm.getAsJsonArray("rolls");
            JsonArray rollGroup = rollSet.get(subIndex).getAsJsonArray();

            rollGroup.forEach((item) -> {
                JsonObject roll = item.getAsJsonObject();
                output.add(roll.get("expression").getAsString());
            });
        }
        else {
            String expression = arr.get(index).getAsString();

            output.add(expression);
        }

        return output;
    }

    public void saveInSheet(JsonObject sheet, String expression) {
        if (category.isAdvanced()) return;

        try {
            SheetLoader.validateSheet(sheet);
            JsonArray arr = sheet.getAsJsonArray(category.toString());
            if (index >= arr.size()) arr.add(expression);
            else arr.set(index, new JsonPrimitive(expression));

            sheet.add(category.toString(), arr);

        }
        catch (Exception e) {
            log.error("e: ", e);
        }
    }

    public void saveInSheet(JsonObject sheet, List<Pair<String, String>> data) {
        saveInSheet(sheet, data, "");
    }

    public void saveInSheet(JsonObject sheet, String formName, boolean isSavingName) {
        if (!isSavingName) saveInSheet(sheet, formName);

        if (!category.isAdvanced()) return;

        try {
            SheetLoader.validateSheet(sheet);
            JsonArray arr = sheet.getAsJsonArray(category.toString());

            JsonObject rollForm; //Entire form, with a name and the 2d rolls array.
            JsonArray rollSet; //Sets of groups of rolls. This is a 2d array.

            if (index >= arr.size()) {
                rollForm = new JsonObject();
                rollSet = new JsonArray();
                rollForm.add("rolls", rollSet);
            }
            else {
                rollForm = arr.get(index).getAsJsonObject();
            }

            if (!formName.isBlank()) {
                rollForm.addProperty("name", formName);
            }

            if (index >= arr.size()) {
                arr.add(rollForm);
            }
            else {
                arr.set(index, rollForm);
            }

            sheet.add(category.toString(), arr);

        }
        catch(Exception e) {
            log.error("e: ", e);
        }
    }

    public void saveInSheet(JsonObject sheet, List<Pair<String, String>> data, String formName) {
        if (!category.isAdvanced()) return;

        try {
            SheetLoader.validateSheet(sheet);
            JsonArray arr = sheet.getAsJsonArray(category.toString());

            JsonObject rollForm; //Entire form, with a name and the 2d rolls array.
            JsonArray rollSet; //Sets of groups of rolls. This is a 2d array.
            JsonArray rollGroup; //Group of rolls that get rolled together when sent through the roll announcer

            if (index >= arr.size()) {
                rollForm = new JsonObject();
                rollSet = new JsonArray();
                rollForm.add("rolls", rollSet);
            }
            else {
                rollForm = arr.get(index).getAsJsonObject();
                rollSet = rollForm.getAsJsonArray("rolls");
            }

            if (!rollForm.has("name") || !formName.isBlank()) {
                rollForm.addProperty("name", formName);
            }

            if (subIndex >= rollSet.size()) {
                rollGroup = new JsonArray();
                data.forEach((pair) -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("context", pair.first);
                    obj.addProperty("expression", pair.second);
                    rollGroup.add(obj);
                });
                rollSet.add(rollGroup);
            }
            else {
                rollGroup = new JsonArray();
                data.forEach((pair) -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("context", pair.first);
                    obj.addProperty("expression", pair.second);
                    rollGroup.add(obj);
                });
                rollSet.set(subIndex, rollGroup);
            }

            rollForm.add("rolls", rollSet);
            if (index >= arr.size()) {
                arr.add(rollForm);
            }
            else {
                arr.set(index, rollForm);
            }

            sheet.add(category.toString(), arr);

        }
        catch (Exception e) {
            log.error("e: ", e);
        }
    }

    /**
     * <p>This won't return anything if the category is of an advanced type.</p>
     * @return
     */
    private String getBasicContext() {
        String result = "";
        result = switch (category.getInt()) {
            case 0 -> switch (index) {
                case 0 -> "Strength Check";
                case 1 -> "Dexterity Check";
                case 2 -> "Constitution Check";
                case 3 -> "Intelligence Check";
                case 4 -> "Wisdom Check";
                case 5 -> "Charisma Check";
                case 6 -> "Initiative";
                default -> result;
            };
            case 1 -> switch (index) {
                case 0 -> "Strength Save";
                case 1 -> "Dexterity Save";
                case 2 -> "Constitution Save";
                case 3 -> "Intelligence Save";
                case 4 -> "Wisdom Save";
                case 5 -> "Charisma Save";
                default -> result;
            };
            case 2 -> switch (index) {
                case 0 -> "Athletics Check";
                case 1 -> "Acrobatics Check";
                case 2 -> "Sleight of Hand Check";
                case 3 -> "Stealth Check";
                case 4 -> "Arcana Check";
                case 5 -> "History Check";
                case 6 -> "Investigation Check";
                case 7 -> "Nature Check";
                case 8 -> "Religion Check";
                case 9 -> "Animal Handling Check";
                case 10 -> "Insight Check";
                case 11 -> "Medicine Check";
                case 12 -> "Perception Check";
                case 13 -> "Survival Check";
                case 14 -> "Deception Check";
                case 15 -> "Intimidation Check";
                case 16 -> "Performance Check";
                case 17 -> "Persuasion Check";
                default -> result;
            };
            default -> "";
        };
        return result;
    }
}