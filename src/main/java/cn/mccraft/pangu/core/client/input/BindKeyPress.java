package cn.mccraft.pangu.core.client.input;

import net.minecraftforge.client.settings.KeyModifier;

import java.lang.annotation.*;

/**
 * Bind key pressed to method.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindKeyPress {
    /**
     * @return the key description, generally start with "key."
     */
    String description();

    /**
     * @return the code of the key to bind from {@link org.lwjgl.input.Keyboard}
     */
    int keyCode();

    KeyModifier modifier() default KeyModifier.NONE;

    /**
     * @return the key category, generally start with "key.categories.",
     * etc. {@link KeyBindingHelper#CATEGORY_CREATIVE}
     */
    String category() default KeyBindingHelper.CATEGORY_MISC;

    /**
     * @return whether responding in game
     */
    boolean enableInGame() default true;

    /**
     * @return whether responding in GUI
     */
    boolean enableInGUI() default true;
}
