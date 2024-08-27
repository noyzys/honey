package dev.shiza.honey.adventure;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import dev.shiza.honey.Honey;
import dev.shiza.honey.message.MessageCompiler;
import dev.shiza.honey.placeholder.evaluator.PlaceholderContext;
import dev.shiza.honey.placeholder.evaluator.PlaceholderEvaluator;
import dev.shiza.honey.placeholder.resolver.PlaceholderResolver;
import dev.shiza.honey.placeholder.sanitizer.PlaceholderSanitizer;
import dev.shiza.honey.reflection.ReflectivePlaceholderEvaluatorFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public interface AdventureHoney extends Honey<Component> {

  static AdventureHoney createReflective() {
    return createReflective(miniMessage());
  }

  static AdventureHoney createReflective(final MiniMessage miniMessage) {
    return createReflective(miniMessage, PlaceholderContext.create());
  }

  static AdventureHoney createReflective(
      final MiniMessage miniMessage, final PlaceholderContext globalContext) {
    return create(
        AdventureMessageCompilerFactory.create(miniMessage),
        globalContext,
        PlaceholderResolver.create(),
        PlaceholderSanitizer.create(),
        ReflectivePlaceholderEvaluatorFactory.create());
  }

  private static AdventureHoney create(
      final MessageCompiler<Component> messageCompiler,
      final PlaceholderContext globalContext,
      final PlaceholderResolver placeholderResolver,
      final PlaceholderSanitizer placeholderSanitizer,
      final PlaceholderEvaluator placeholderEvaluator) {
    return new AdventureHoneyImpl(
        messageCompiler,
        globalContext,
        placeholderResolver,
        placeholderSanitizer,
        placeholderEvaluator);
  }
}
