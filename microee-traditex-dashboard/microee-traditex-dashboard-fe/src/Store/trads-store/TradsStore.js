import { action } from 'mobx';
import { HttpWrapper } from 'reack-lang';
import queryString from 'query-string';

export class TradsStore {

    @action getPricing(done) {
        HttpWrapper.create()({
            method: 'get',
            url: '/trads/getPricing',
            data: null,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function (res) {
            done(res.data);
        });
    }

    @action getBalances(type, currency, done) {
        HttpWrapper.create()({
            method: 'get',
            url: '/trads/getBalances?' + queryString.stringify({type: type, currency: currency}, {sort: false}),
            data: null,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function (res) {
            done(res.data);
        });
    }

    @action getAccountBalances(type, currency, done) {
        HttpWrapper.create()({
            method: 'get',
            url: '/trads/getAccountBalances?' + queryString.stringify({type: type, currency: currency}, {sort: false}),
            data: null,
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function (res) {
            done(res.data);
        });
    }

}

export default new TradsStore();
