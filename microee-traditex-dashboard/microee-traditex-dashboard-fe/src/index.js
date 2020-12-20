import React from 'react';
import ReactDOM from 'react-dom';
import * as serviceWorker from './serviceWorker';


import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import IndexPage from "__pages/index/IndexPage";


const SliderComponent = () => (
    <Switch>
        <Route exact path={['/', '/index']} component={ IndexPage } />
    </Switch>
)

ReactDOM.render(
    <Router>
        <SliderComponent />
    </Router>,
     document.getElementById('root'));

serviceWorker.unregister();
