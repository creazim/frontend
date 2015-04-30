define([
    'helpers/injector'
], function (
    Injector
) {
    Injector = Injector.default;

    return new Injector()
        .store(['common/utils/config', 'common/utils/storage'])
        .require(['common/modules/commercial/third-party-tags/audience-science-gateway',
                  'common/utils/config',
                  'common/utils/storage'], function (audienceScienceGateway, config, storage) {

            var getParaWithSpaceStub, $fixturesContainer;

            describe('Audience Science Gateway', function () {

                beforeEach(function () {
                    config.page = {
                        section: 'news'
                    };
                    config.switches = {
                        audienceScienceGateway: true
                    };
                });

                it('should be able to get segments', function () {
                    audienceScienceGateway.init();
                    var stored      = {},
                        storedValue = {
                            Y1C40a: {
                                'default': {
                                    key: 'sct-76-qHDJGkNzOZB198Rq0V98'
                                },
                                data: {
                                    'STPG-li383': {
                                        key: 'sct-76-qHDJGkNzOZB198Rq0V98'
                                    }
                                },
                                blob: 'CjRjMmMyOTQ1OC0yZDhmLTRlMGMtYTY1Ni04NjcxZTJmOWRmMzctMTQwODYyODU1MjA2Ny0y'
                            },
                            c7Zrhu: {
                                'default': {
                                    key: 'k_1zMBS3xPySFDHSOzVC3M3um3A'
                                },
                                data: {
                                    'STPG-li436': {
                                        key: 'k_1zMBS3xPySFDHSOzVC3M3um3A'
                                    }
                                },
                                blob: 'CjQ3YmM2YjgzZC02ZTk4LTQ3MjEtYTVmZC05ZGJiYzcwZmQyOWItMTQwODYzMjE4NTI4MS0w'
                            }
                        };
                    stored['news'] = storedValue;
                    storage.local.set('gu.ads.audsci-gateway', stored);
                    expect(audienceScienceGateway.getSegments()).toEqual({
                        pq_Y1C40a: 'T',
                        pq_c7Zrhu: 'T'
                    });
                });

                it('should return empty object if no segments', function () {
                    audienceScienceGateway.init();
                    storage.local.set('gu.ads.audsci-gateway', {});
                    expect(audienceScienceGateway.getSegments()).toEqual({});
                });

                it('should return empty object if switch is off', function () {
                    config.page.section = undefined;
                    audienceScienceGateway.init();
                    expect(audienceScienceGateway.getSegments()).toEqual({});
                });

            });

        });

});
