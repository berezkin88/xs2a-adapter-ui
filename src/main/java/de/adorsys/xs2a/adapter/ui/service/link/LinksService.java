package de.adorsys.xs2a.adapter.ui.service.link;

import de.adorsys.xs2a.adapter.model.HrefTypeTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinksService {
    private static final String AUTHORISATION_REGEX = "/authorisations/(.+)/";
    private static final Pattern AUTHORISATION_PATTERN = Pattern.compile(AUTHORISATION_REGEX);

    public boolean isEmbeddedApproach(Map<String, HrefTypeTO> links) {
        return !isRedirectApproach(links) && !isOauthApproach(links);
    }

    public boolean isRedirectApproach(Map<String, HrefTypeTO> links) {
        return links.containsKey("scaRedirect");
    }

    public boolean isOauthApproach(Map<String, HrefTypeTO> links) {
        return links.containsKey("scaOAuth");
    }

    public boolean authorisationNotStarted(Map<String, HrefTypeTO> links) {
        if (links.containsKey("startAuthorization")) {
            return true;
        }

        String authLink;
        if (links.containsKey("startAuthorisationWithPsuAuthentication")) {
            authLink = links.get("startAuthorisationWithPsuAuthentication").getHref();
        } else if (links.containsKey("startAuthorisationWithEncryptedPsuAuthentication")) {
            authLink = links.get("startAuthorisationWithEncryptedPsuAuthentication").getHref();
        } else {
            return false;
        }

        return getAuthorisationIdFromLink(authLink).isEmpty();
    }

    public boolean startAuthorisationWithPsuAuthentication(Map<String, HrefTypeTO> links) {
        return links.containsKey("startAuthorisationWithPsuAuthentication")
                       || links.containsKey("startAuthorisationWithEncryptedPsuAuthentication");
    }

    public boolean startAuthorisationEncrypted(Map<String, HrefTypeTO> links) {
        return links.containsKey("startAuthorisationWithEncryptedPsuAuthentication");
    }

    public String getAuthorisationId(Map<String, HrefTypeTO> links) {
        String authLink;

        if (links.containsKey("updatePsuAuthentication")) {
            authLink = links.get("updatePsuAuthentication").getHref();
        } else if (links.containsKey("startAuthorisationWithPsuAuthentication")) {
            authLink = links.get("startAuthorisationWithPsuAuthentication").getHref();
        } else if (links.containsKey("startAuthorisationWithEncryptedPsuAuthentication")) {
            authLink = links.get("startAuthorisationWithEncryptedPsuAuthentication").getHref();
        } else {
            // TODO change for REDIRECT and OAUTH approaches if needed
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        return getAuthorisationIdFromLink(authLink)
                       // TODO change to some more appropriate exception
                       .orElseThrow(RuntimeException::new);
    }

    private Optional<String> getAuthorisationIdFromLink(String link) {
        Matcher authorisationMatcher = AUTHORISATION_PATTERN.matcher(link);

        if (authorisationMatcher.find()) {
            return Optional.of(authorisationMatcher.group(1));
        }

        return Optional.empty();
    }
}
