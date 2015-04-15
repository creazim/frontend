define([
    'common/utils/detect',
    'common/utils/template',
    'common/modules/ui/message',
    'common/views/svgs',
    'text!common/views/donot-use-adblock.html'
], function (
    detect,
    template,
    Message,
    svgs,
    doNotUseAdblockTemplate
) {
    return function () {
        this.id = 'AdBlock';
        this.start = '2015-03-27';
        this.expiry = '2015-04-27';
        this.author = 'Zofia Korcz';
        this.description = 'Test if the users will disable adblock or at least click support link';
        this.audience = 0.5;
        this.audienceOffset = 0;
        this.successMeasure = 'Users will disable adblock or at least click support link';
        this.audienceCriteria = 'All users';
        this.dataLinkNames = 'adblock message, hide, read more';
        this.idealOutcome = 'Users will disable adblock on theguardian site';

        this.canRun = function () {
            return true;
        };

        this.variants = [
            {
                id: 'A',
                test: function () {
                    var adblockLink = 'https://membership.theguardian.com/about/supporter?INTCMP=adb-ma';

                    if (detect.getBreakpoint() !== 'mobile' && detect.adblockInUse()) {
                        new Message('adblock', {
                            pinOnHide: false,
                            siteMessageLinkName: 'adblock message A',
                            siteMessageCloseBtn: 'hide'
                        }).show(template(
                            doNotUseAdblockTemplate,
                            {
                                adblockLink: adblockLink,
                                messageText: 'If you\'re reading the Guardian without ads, why not join the Guardian instead?',
                                linkText: 'Become a supporter today',
                                arrowWhiteRight: svgs('arrowWhiteRight')
                            }
                        ));
                    }
                }
            },
            {
                id: 'B',
                test: function () {
                    var adblockLink = 'https://membership.theguardian.com/about/supporter?INTCMP=adb-mb';

                    if (detect.getBreakpoint() !== 'mobile' && detect.adblockInUse()) {
                        new Message('adblock', {
                            pinOnHide: false,
                            siteMessageLinkName: 'adblock message B',
                            siteMessageCloseBtn: 'hide'
                        }).show(template(
                            doNotUseAdblockTemplate,
                            {
                                adblockLink: adblockLink,
                                messageText: 'We notice you\'ve got an ad-blocker switched on. Perhaps you\'d like to support the Guardian another way?',
                                linkText: 'Become a supporter today',
                                arrowWhiteRight: svgs('arrowWhiteRight')
                            }
                        ));
                    }
                }
            }
        ];
    };

});
