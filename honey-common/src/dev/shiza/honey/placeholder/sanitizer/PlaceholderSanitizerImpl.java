package dev.shiza.honey.placeholder.sanitizer;

import dev.shiza.honey.placeholder.evaluator.EvaluatedPlaceholder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PlaceholderSanitizerImpl implements PlaceholderSanitizer {

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");
  private static final Pattern METHOD_CALL_PATTERN = Pattern.compile("\\b\\w+(?:\\.\\w+)*");
  private static final Pattern TAG_PATTERN = Pattern.compile("[!?#]?[a-z0-9_-]*");
  private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
  private static final Pattern PARANTHESE_PATTERN = Pattern.compile("\\(\\)");
  private static final Pattern ALPHANUMERICAL_PATTERN = Pattern.compile("\\W");
  private static final Pattern TRANSFORMATION_PATTERN = Pattern.compile("[^a-z0-9_-]");
  private static final char TAG_RESOLVER_INIT = '<';
  private static final char TAG_RESOLVER_STOP = '>';

  PlaceholderSanitizerImpl() {}

  @Override
  public String getSanitizedContent(
      final String content, final List<SanitizedPlaceholder> placeholders) {
    String sanitizedContent = content;
    for (final SanitizedPlaceholder placeholder : placeholders) {
      sanitizedContent =
          sanitizedContent.replace(placeholder.expression(), getResolvableTag(placeholder.key()));
    }
    return sanitizedContent;
  }

  @Override
  public SanitizedPlaceholder getSanitizedPlaceholder(final EvaluatedPlaceholder placeholder) {
    final Matcher matcher = PLACEHOLDER_PATTERN.matcher(placeholder.placeholder().key());
    while (matcher.find()) {
      final String expression = matcher.group(1);
      final String sanitizedExpression = getSanitizedExpression(expression);
      if (TAG_PATTERN.matcher(sanitizedExpression).matches()) {
        return new SanitizedPlaceholder(
            sanitizedExpression, placeholder.placeholder().key(), placeholder.evaluatedValue());
      }
    }

    throw new PlaceholderSanitizationException(
        "Could not sanitize placeholder with key: %s".formatted(placeholder.placeholder().key()));
  }

  @Override
  public List<SanitizedPlaceholder> getSanitizedPlaceholders(
      final List<EvaluatedPlaceholder> placeholders) {
    final List<SanitizedPlaceholder> sanitizedPlaceholders = new ArrayList<>();
    for (final EvaluatedPlaceholder placeholder : placeholders) {
      sanitizedPlaceholders.add(getSanitizedPlaceholder(placeholder));
    }
    return sanitizedPlaceholders;
  }

  private String getSanitizedExpression(final String expression) {
    int lastEnd = 0;

    final StringBuilder result = new StringBuilder();
    final Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(expression);
    while (methodCallMatcher.find()) {
      result.append(expression, lastEnd, methodCallMatcher.start());
      String transformedMethodCall = methodCallMatcher.group();
      transformedMethodCall = PARANTHESE_PATTERN.matcher(transformedMethodCall).replaceAll("");
      transformedMethodCall = DOT_PATTERN.matcher(transformedMethodCall).replaceAll("");
      transformedMethodCall = ALPHANUMERICAL_PATTERN.matcher(transformedMethodCall).replaceAll("");
      transformedMethodCall = transformedMethodCall.toLowerCase(Locale.ROOT);
      result.append(transformedMethodCall);

      lastEnd = methodCallMatcher.end();
    }
    result.append(expression.substring(lastEnd));

    String transformedExpression = result.toString();
    transformedExpression = TRANSFORMATION_PATTERN.matcher(transformedExpression).replaceAll("");
    transformedExpression = transformedExpression.toLowerCase(Locale.ROOT);
    return transformedExpression;
  }

  private String getResolvableTag(final String key) {
    return TAG_RESOLVER_INIT + key + TAG_RESOLVER_STOP;
  }
}
