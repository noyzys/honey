package dev.shiza.honey.message.compiler;

import dev.shiza.honey.placeholder.sanitizer.SanitizedPlaceholder;
import java.util.List;

@FunctionalInterface
public interface MessageCompiler<T> {

  T compile(final String sanitizedContent, final List<SanitizedPlaceholder> placeholders);
}
