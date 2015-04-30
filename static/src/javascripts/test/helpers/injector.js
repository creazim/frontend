"format es6";

export default class Injector {
    constructor(loader) {
        this.loader = System.clone();
        // TODO: Tidy up
        this.loader.paths = System.paths;
        this.loader.map = System.map;
        this.loader.normalize = System.normalize;
        this.loader.transpiler = System.transpiler;
    }

    mock(mocks) {
        // Support alternative syntax
        if (typeof mocks === 'string') {
            mocks = { [mocks]: arguments[1] }
        }
        Object.keys(mocks).forEach(moduleId => {
            var mock = mocks[moduleId];
            // TODO: Is there a proper way to lookup in the map?
            var mappedModuleId = System.map[moduleId] || moduleId;
            this.loader.register(mappedModuleId, [], true, () => mock)
        });
        return this;
    }

    require(dependencies, fn) {
        return Promise.all(dependencies.map(dep => this.loader.import(dep)))
            .then(args => fn(...args))
    }

    store() { return this; }
}
