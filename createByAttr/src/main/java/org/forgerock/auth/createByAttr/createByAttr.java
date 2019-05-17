            /*
             * The contents of this file are subject to the terms of the Common Development and
             * Distribution License (the License). You may not use this file except in compliance with the
             * License.
             *
             * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
             * specific language governing permission and limitations under the License.
             *
             * When distributing Covered Software, include this CDDL Header Notice in each file and include
             * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
             * Header, with the fields enclosed by brackets [] replaced by your own identifying
             * information: "Portions copyright [year] [name of copyright owner]".
             *
             * Copyright 2018 ForgeRock AS.
             */


            package org.forgerock.auth.createByAttr;

    import com.google.inject.assistedinject.Assisted;
    import com.iplanet.sso.SSOException;
    import com.sun.identity.idm.*;
    import com.sun.identity.shared.debug.Debug;
    import org.forgerock.json.JsonValue;
    import org.forgerock.openam.annotations.sm.Attribute;
    import org.forgerock.openam.auth.node.api.*;
    import org.forgerock.openam.core.CoreWrapper;

    import javax.inject.Inject;
    import javax.lang.model.type.NullType;

    import java.util.*;

    import static org.forgerock.openam.auth.node.api.SharedStateConstants.*;

    /**
     * A node that checks to see if zero-page login headers have specified username and shared key
     * for this request.
     */
    @Node.Metadata(outcomeProvider  = AbstractDecisionNode.OutcomeProvider.class,
            configClass      = createByAttr.Config.class)
    public class createByAttr extends AbstractDecisionNode {

        private final Config config;
        private final CoreWrapper coreWrapper;
        private final static String DEBUG_FILE = "createByAttr";
        private static String mySharedSecret = "";
        private static String searchAttribute = "";
        private static String sharedStateVar = "";
        private static String sharedSecret = "";
        private static String attrValue = "";
        private static String username = "";

        protected Debug debug = Debug.getInstance(DEBUG_FILE);

        /**
         * Configuration for the node.
         */
        public interface Config {
            @Attribute(order = 100) default String sharedStateVar() {
                return "restrictedID";
            }
            @Attribute(order = 200) default String passwordAttribute() {
              return "userPassword";
            }
            @Attribute(order = 300) default String defaultPwd() {
                return "ITZBund!123";
            }
            @Attribute(order = 400) default String storingAttribute() { return "emplyoeeNumber"; }
        }


        /**
         * Create the node.
         * @param config The service config.
         * @throws NodeProcessException If the configuration was not valid.
         */
        @Inject
        public createByAttr(@Assisted Config config, CoreWrapper coreWrapper) throws NodeProcessException {
            this.config = config;
            this.coreWrapper = coreWrapper;
        }

        public void debugmessage(String s) {
            System.out.println(s);
        }

        @Override
        public Action process(TreeContext context) throws NodeProcessException {
            debugmessage("[" + DEBUG_FILE + "]: createByAttr started ...");
            debugmessage("[" + DEBUG_FILE + "]: Configuration: shared state attribute: '" + config.sharedStateVar() + "'.");
            debugmessage("[" + DEBUG_FILE + "]: Configuration: default password: '" + config.defaultPwd() + "'.");

            attrValue = context.sharedState.get(config.sharedStateVar()).toString().replace("\"", "");

            debugmessage("[" + DEBUG_FILE + "]: Found restricted ID: '" + attrValue + "'.");

            debugmessage("[" + DEBUG_FILE + "]: Will create user with BaseDN: '" + coreWrapper.convertRealmPathToRealmDn(context.sharedState.get(REALM).asString()) + "'.");
            debugmessage("[" + DEBUG_FILE + "]: and id: '" + attrValue + "'.");

            debugmessage("[" + DEBUG_FILE + "]: and '" + config.passwordAttribute() + "' set to '" + config.defaultPwd() + "'.");
            debugmessage("[" + DEBUG_FILE + "]: and value of '" + config.sharedStateVar() + "' will be written to '" + config.storingAttribute() + "' in user's profile.");

            Set<String> pwdValue = new HashSet<>();
            pwdValue.add(config.defaultPwd());

            Set<String> pseudonymValue = new HashSet<>();
            pseudonymValue.add(attrValue);

            Map<String, Set> attrMap = new HashMap<>();
            attrMap.put(config.passwordAttribute(), pwdValue);
            attrMap.put(config.storingAttribute(), pseudonymValue);

            AMIdentityRepository idrepo = coreWrapper.getAMIdentityRepository(
                    coreWrapper.convertRealmPathToRealmDn(context.sharedState.get(REALM).asString()));
            try {
                AMIdentity id = idrepo.createIdentity(IdType.USER, attrValue, attrMap);
                debugmessage("[" + DEBUG_FILE + "]: User successfully created.");
                // context.sharedState.put(USER_GOTO_PARAM_KEY, "http://www.google.de");
                return goTo(true).build();
            } catch (IdRepoException e) {
            e.printStackTrace();
            } catch (SSOException e) {
            e.printStackTrace();
            }
            return goTo(false).build();
        }
    }