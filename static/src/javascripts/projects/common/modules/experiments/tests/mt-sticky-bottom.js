define([
    'fastdom',
    'common/utils/$',
    'common/utils/_',
    'common/utils/config',
    'common/utils/detect',
    'common/utils/mediator'
], function (
    fastdom,
    $,
    _,
    config,
    detect,
    mediator
) {
    return function () {
        this.id = 'MtStickyBtm';
        this.start = '2015-03-26';
        this.expiry = '2015-05-26';
        this.author = 'Zofia Korcz';
        this.description = 'Top ad slot is sticky at the bottom of page';
        this.audience = 0.02;
        this.audienceOffset = 0.4;
        this.successMeasure = '';
        this.audienceCriteria = '1% of US edition';
        this.dataLinkNames = '';
        this.idealOutcome = '';

        this.canRun = function () {
            return config.page.edition === 'US' && config.page.isFront && detect.getBreakpoint() !== 'mobile';
        };

        function updatePosition(stickyConfig, containerNo) {
            fastdom.write(function () {
                var bannerScrollPos,
                    stickyBannerHeight;

                //we need to check it every time during scroll because we don't know when there will be a respond from DFP
                stickyBannerHeight = stickyConfig.$stickyBanner.dim().height;

                //add a proper padding between the nth and nth + 1 container
                $(stickyConfig.$container.get(containerNo)).css({
                    'padding-top': stickyBannerHeight
                });

                bannerScrollPos = window.scrollY + stickyConfig.windowHeight - stickyBannerHeight;

                //leave the banner behind when we will scroll to the end of the nth container
                if (bannerScrollPos >= stickyConfig.containerOffset) {
                    stickyConfig.$stickyBanner.css({
                        position: 'absolute',
                        top: stickyConfig.containerOffset,
                        bottom: null,
                        width: '100%',
                        'z-index': '1001',
                        'border-top': '#ccc 1px solid',
                        'border-bottom': '#ccc 1px solid'
                    });
                } else {
                    stickyConfig.$stickyBanner.css({
                        position: 'fixed',
                        bottom: 0,
                        top: null,
                        width: '100%',
                        'z-index': '1001',
                        'border-top': '#ccc 1px solid'
                    });
                }
            });
        }

        function setPosition(stickyConfig, containerNo) {
            fastdom.write(function () {
                $('.fc-container__inner', $(stickyConfig.$container.get(containerNo))).css('border-top', 'none');

                if (detect.getBreakpoint() === 'mobile') {
                    stickyConfig.$stickyBanner = stickyConfig.$stickyTopAdMobile;
                } else {
                    stickyConfig.$stickyBanner = stickyConfig.$stickyTopAd;
                }

                //banner is sticky at the bottom of the page
                stickyConfig.$stickyBanner.css({
                    position: 'fixed',
                    bottom: 0,
                    top: null,
                    width: '100%',
                    'z-index': '1001',
                    'border-top': '#ccc 1px solid'
                });

                stickyConfig.containerOffset = getBouncing(stickyConfig.$container.get(containerNo));
            });
        }

        function getBouncing(elem) {
            return elem.getBoundingClientRect().top;
        }

        this.fireStickyBottom = function () {
            fastdom.read(function () {
                var stickyConfig = {
                        $stickyTopAd: $('.top-banner-ad-container--desktop'),
                        $stickyTopAdMobile: $('.top-banner-ad-container--mobile'),
                        $container: $('.fc-container'),
                        headerHeight: $('#header').dim().height,
                        windowHeight: window.innerHeight || document.documentElement.clientHeight
                    },
                    containerNo = config.page.contentType === 'Section' ? 2 : 1; //leave banner between the nth and nth+1 container

                //we need at least nth + 1 containers
                if (stickyConfig.windowHeight <= 960 && stickyConfig.$container.length >= containerNo + 1 && !window.scrollY) {
                    setPosition(stickyConfig, containerNo);

                    mediator.on('window:scroll', _.throttle(function () {
                        updatePosition(stickyConfig, containerNo);
                    }, 10));
                }
            });
        };

        this.variants = [
            {
                id: 'A',
                test: function () { }
            },
            {
                id: 'B',
                test: function () { }
            }
        ];
    };

});
