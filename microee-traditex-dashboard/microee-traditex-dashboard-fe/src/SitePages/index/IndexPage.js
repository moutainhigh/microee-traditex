import React, { Component } from 'react';
import styled, { css } from "styled-components";
import { Fake } from 'reack-fake';
import SockJsClient from 'react-stomp';

import TradsStore from "__store/trads-store/TradsStore";

const moment = Fake('moment');
const _l = Fake('_');
const jQuery = Fake('jQuery');

const the_topics = {
      timer: '/topic/timer', 
      pricing: '/topic/__traditex.message.pricing', 
      orderbook: '/topic/__traditex.message.orderbook', 
      diskorders: '/topic/__traditex.message.diskorders', 
      httpnetwork: '/topic/__traditex.message.httpnetwork',
      httplog: '/topic/__traditex.message.httplog.listener',
      revokeorder: '/topic/__traditex.message.revokeorder.count',
      accountBalance: '/topic/__traditex.message.account.balance',
      diskAccount: '/topic/__traditex.message.disk-account.balancess',
      solrAccount: '/topic/__traditex.message.solr-account.balancess'
};

export class _IndexPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
          accountDiskBalance: {

          },
          accountSolrBalance: {

          },
          httpNetworkLogger: {
            speed: 0,
            max: 0,
            min: 0,
            items: []
          }
        };
    }

  static getDerivedStateFromProps(props, state) {
      return {
        ...state,
        theTopicsDef: {
              timer: the_topics.timer, 
              pricing: jQuery('#topic_pricing').val() ? jQuery('#topic_pricing').val() : the_topics.pricing, 
              orderbook: jQuery('#topic_orderbook').val() ? jQuery('#topic_orderbook').val() : the_topics.orderbook, 
              diskorders: jQuery('#topic_diskorders').val() ? jQuery('#topic_diskorders').val() : the_topics.diskorders, 
              httpnetwork: jQuery('#topic_httpnetwork').val() ? jQuery('#topic_httpnetwork').val() : the_topics.httpnetwork,
              httplog: jQuery('#topic_httplog').val() ? jQuery('#topic_httplog').val() : the_topics.httplog,
              revokeorder: jQuery('#topic_revokeorder').val() ? jQuery('#topic_revokeorder').val() : the_topics.revokeorder,
              accountBalance: jQuery('#topic_accountBalance').val() ? jQuery('#topic_accountBalance').val() : the_topics.accountBalance,
              diskAccount: jQuery('#topic_diskAccount').val() ? jQuery('#topic_diskAccount').val() : the_topics.diskAccount,
              solrAccount: jQuery('#topic_solrAccount').val() ? jQuery('#topic_solrAccount').val() : the_topics.solrAccount
        }
      };
  }

    getTopicValues(topicObject) {
      let arr = [];
      for (const [key, value] of Object.entries(topicObject)) {
        arr.push(value);
      }
      return arr;
    }

    componentDidMount() {
      TradsStore.getPricing((result) => {
        if (result.code === 200 || result.code === 201) {
          this.setState({
            ...this.state,
            oanda: result.data[0],
            theTopics: this.getTopicValues(this.state.theTopicsDef)
          }, () => {
            console.log(this.state.theTopicsDef, 'theTopics')
          });
        }
      });
      this.queryAccountBalance();
    }

    processDiskAccountBalance(diskAccountBalance) {
      let _btcBalance = diskAccountBalance.filter(m => m.currency === 'btc');
      let _btcBalanceTrad = _btcBalance.filter(m => m.type === 'trade');
      let _btcBalanceFrozen = _btcBalance.filter(m => m.type === 'frozen');
      let _jpyBalance = diskAccountBalance.filter(m => m.currency === 'jpy');
      let _jpyBalanceTrad = _jpyBalance.filter(m => m.type === 'trade');
      let _jpyBalanceFrozen = _jpyBalance.filter(m => m.type === 'frozen');
      let btcBalance = { tradeBalance: _btcBalanceTrad.length > 0 ? parseFloat(_btcBalanceTrad[0].balance): 0.0, frozenBalance: _btcBalanceFrozen.length > 0 ? parseFloat(_btcBalanceFrozen[0].balance) : 0.0 };
      let jpyBalance = { tradeBalance: _jpyBalanceTrad.length > 0 ? parseFloat(_jpyBalanceTrad[0].balance): 0.0, frozenBalance: _jpyBalanceFrozen.length > 0 ? parseFloat(_jpyBalanceFrozen[0].balance) : 0.0 };
      let btcBalances = {
        ...btcBalance,
        rate: ((btcBalance.frozenBalance / btcBalance.tradeBalance) * 100).toFixed(4) + '%'
      }
      let jpyBalances = {
        ...jpyBalance,
        rate: ((jpyBalance.frozenBalance / jpyBalance.tradeBalance) * 100).toFixed(4) + '%'
      }
      this.setState({
        ...this.state,
        accountDiskBalancesBTC: btcBalances,
        accountDiskBalancesJPY: jpyBalances
      });
    }

    processSolrAccountBalance(diskAccountBalance) {
      let _btcBalance = diskAccountBalance.filter(m => m.currency === 'btc');
      let _btcBalanceTrad = _btcBalance.filter(m => m.type === 'trade');
      let _btcBalanceFrozen = _btcBalance.filter(m => m.type === 'frozen');
      let _usdtBalance = diskAccountBalance.filter(m => m.currency === 'usdt');
      let _usdtBalanceTrad = _usdtBalance.filter(m => m.type === 'trade');
      let _usdtBalanceFrozen = _usdtBalance.filter(m => m.type === 'frozen');
      let btcBalance = { tradeBalance: _btcBalanceTrad.length > 0 ? parseFloat(_btcBalanceTrad[0].balance): 0.0, frozenBalance: _btcBalanceFrozen.length > 0 ? parseFloat(_btcBalanceFrozen[0].balance) : 0.0 };
      let usdtBalance = { tradeBalance: _usdtBalanceTrad.length > 0 ? parseFloat(_usdtBalanceTrad[0].balance): 0.0, frozenBalance: _usdtBalanceFrozen.length > 0 ? parseFloat(_usdtBalanceFrozen[0].balance) : 0.0 };
      let btcBalances = {
        ...btcBalance,
        rate: ((btcBalance.frozenBalance / btcBalance.tradeBalance) * 100).toFixed(4) + '%'
      }
      let usdtBalances = {
        ...usdtBalance,
        rate: ((usdtBalance.frozenBalance / usdtBalance.tradeBalance) * 100).toFixed(4) + '%'
      }
      this.setState({
        ...this.state,
        accountSolrBalancesBTC: btcBalances,
        accountSolrBalancesUSDT: usdtBalances
      });
    }

    queryAccountBalance() {
      
      TradsStore.getAccountBalances('disk', ['btc', 'jpy'], (result) => {
        if (result.code === 200 || result.code === 201) {
          this.processDiskAccountBalance(result.data);
        }
      });

      TradsStore.getAccountBalances('solr', ['btc', 'usdt'], (result) => {
        if (result.code === 200 || result.code === 201) {
          this.processSolrAccountBalance(result.data);
        }
      });
    }

    // 处理 websocket 消息
    onWebSocketMessage(message, topic) {

      if (topic === this.state.theTopicsDef.timer) {
        this.setState({
          ...this.state,
          serverTimer: moment(message).format('YYYY-MM-DD HH:mm:ss')
        })
        return;
      }

      if (topic === this.state.theTopicsDef.orderbook) {
        let _message=JSON.parse(message);
        this.setState({
          ...this.state,
          orderbook: {
            symbol: _message.symbol,
            asks: JSON.parse(_message['asks']),
            bids: JSON.parse(_message['bids'])
          }
        });
        return;
      }

      if (topic === this.state.theTopicsDef.pricing) {
        this.setState({
          ...this.state,
          oanda: JSON.parse(message)
        });
        return;
      }

      if (topic === this.state.theTopicsDef.diskorders) {
        let _diskorders = this.processDiskOrders(JSON.parse(message));
        this.setState({
          ...this.state,
          diskorders: _diskorders
        });
        return;
      }

      if (topic === this.state.theTopicsDef.accountBalance) {
        let _accountBalance = JSON.parse(message);
        let _balanceList = _accountBalance.list;
        let accountDiskBalance = this.state.accountDiskBalance;
        let accountSolrBalance = this.state.accountSolrBalance;
        for (let b of _balanceList) {
          let _currency = b.currency;
          if (_accountBalance.type === 'disk') {
            accountDiskBalance[_currency] = b.balance;
          }
          if (_accountBalance.type === 'solr') {
            accountSolrBalance[_currency] = b.balance;
          }
        }
        this.setState({
          ...this.state,
          accountDiskBalance: accountDiskBalance,
          accountSolrBalance: accountSolrBalance
        });
        return;
      }

      if (topic === this.state.theTopicsDef.diskAccount) {
        let _accountBalance = JSON.parse(message);
        this.processDiskAccountBalance(_accountBalance);
        return;
      }

      if (topic === this.state.theTopicsDef.solrAccount) {
        let _accountBalance = JSON.parse(message);
        this.processSolrAccountBalance(_accountBalance);
        return;
      }

      if (topic === this.state.theTopicsDef.revokeorder) {
        let _revokeCount = JSON.parse(message);
        let _diskorders = this.state.diskorders;
        for (const [key, value] of Object.entries(_revokeCount)) {
          if (!_diskorders || _diskorders.length === 0) {
            continue;
          }
          for (let order of _diskorders) {
            if (order.orderId === key) {
              order.revokeCount = value;
            }
          }
        }
        this.setState({
          ...this.state,
          revokeCount: _revokeCount,
          diskorders: _diskorders
        });
        return;
      }

      if (topic === this.state.theTopicsDef.httplog) {
        let _httpnetworkLogger = message;
        let _speed = _httpnetworkLogger.speed;
        let _max = this.state.httpNetworkLogger.max;
        let _min = this.state.httpNetworkLogger.min;
        let _items = this.state.httpNetworkLogger.items;
        _items.push(_httpnetworkLogger);
        //console.log(_httpnetworkLogger, '_httpnetworkLogger');
        if (_max < _httpnetworkLogger.speed) {
          _max = _httpnetworkLogger.speed;
        }
        if (_min === 0 || _speed < _min) {
          _min = _speed;
        } 
        this.setState({
          ...this.state,
          httpNetworkLogger: {
            speed: _speed,
            max: _max,
            min: _min,
            items: _items.slice(_items.length > 15 ? _items.length - 15 : 0)
          }
        });
        return;
      }

      console.log(message, topic);
    }

    processDiskOrders(diskorders) {
      let _diskorders = [];
      for (const [key, value] of Object.entries(diskorders)) {
        let distOrderParam = JSON.parse(value)['distOrderParam'];
        _diskorders.push({
          pick: distOrderParam.pick,
          orderId: key,
          side: distOrderParam.side,
          symbol: distOrderParam.symbol,
          amount: parseFloat(distOrderParam.amount),
          price: parseFloat(distOrderParam.price),
          revokeCount: 0
        });
      }
      return _l.orderBy(_diskorders, ['pick'],['asc']);
    }

    getOrderBookAsksSection() {
      return (
            this.state.orderbook.asks.map((item, i) => {
              return <li key={i}>{item[0]} - {item[1]}</li>;
            })
        );
    }

    getOrderBookBidsSection() {
      return (
            this.state.orderbook.bids.map((item, i) => {
              return <li key={i}>{item[0]} - {item[1]}</li>;
            })
        );
    }

    getDiskOrdersAsksSection() {
      return (
            this.state.diskorders.filter(f => f.side === 'SELL' ).map((item, i) => {
              return <li key={i}>[{item['revokeCount']}/{item['pick']}]-{item['orderId']} - {item['price']} - {item['amount']}</li>;
            })
        );
    }

    getDiskOrdersBidsSection() {
      return (
            this.state.diskorders.filter(f => f.side === 'BUY' ).map((item, i) => {
              return <li key={i}>{item['price']} - {item['amount']} - {item['orderId']}-[{item['revokeCount']}/{item['pick']}] </li>;
            })
        );
    }

    getHttpNetworkSection() {
      return (
        this.state.httpNetworkLogger.items.map((m, i) => {
          return <div key={i}> 
            <span className="time">{ moment(m.start).format('HH:mm:ss.SSS') }</span>
            <span className="speed">{ m.speed }</span>
            <span className="method">{ m.method.toUpperCase() }</span>
            <span className="url">{ m.URL }</span> </div>;
        })
      );
    }

    render() {
      const { httpNetworkLogger, accountDiskBalancesBTC, accountDiskBalancesJPY, accountSolrBalancesBTC, accountSolrBalancesUSDT } = this.state;
      return (
        <div className={`${this.props.className} IndexPage`}>
          <h2 id="server_time" style={{color: 'crimson', fontWeight: 'bold', textAlign: 'center'}}>{ this.state.serverTimer }</h2>
          { this.state.httpNetworkLogger && <div className="top-section">
              <div className="httpNetwork-logger">
                <h3 className="httpnetwork-speed">HttpNetwork Interceptor: 当前-<span>{httpNetworkLogger.speed}</span>, 最高-<span>{httpNetworkLogger.max}</span>, 最低-<span>{httpNetworkLogger.min}</span></h3>
                <div className="httpNetwork-logger-items">
                  {this.state.httpNetworkLogger && this.getHttpNetworkSection()}
                </div>
              </div>
            </div>
          }
          <div className="top-section">
            <div className="oanda">
              <h3>Oanda - { this.state.oanda && this.state.oanda.instrument }</h3>
              <h3>卖: <span>{ this.state.oanda && this.state.oanda.sell }</span></h3>
              <h3>买: <span>{ this.state.oanda && this.state.oanda.buy }</span></h3>
            </div>
            <div className="balance account-balance">
              <h3>Disk Account Balance</h3>
              { accountDiskBalancesBTC && <ul> 
                  <li>BTC: {accountDiskBalancesBTC.tradeBalance } / {accountDiskBalancesBTC.frozenBalance } / {accountDiskBalancesBTC.rate }</li>
                  <li>JPY: {accountDiskBalancesJPY.tradeBalance } / {accountDiskBalancesJPY.frozenBalance } / {accountDiskBalancesJPY.rate }</li>
                </ul> }
            </div>
            <div className="balance account-balance">
              <h3>Solr Account Balance</h3>
              { accountSolrBalancesBTC && <ul> 
                  <li>BTC: {accountSolrBalancesBTC.tradeBalance } / {accountSolrBalancesBTC.frozenBalance } / {accountSolrBalancesBTC.rate }</li>
                  <li>USDT: {accountSolrBalancesUSDT.tradeBalance } / {accountSolrBalancesUSDT.frozenBalance } / {accountSolrBalancesUSDT.rate }</li>
                </ul> }
            </div>
            <div className="clear"></div>
          </div>
          <div className="orderbook">
            <h3>OrderBook - { this.state.orderbook && this.state.orderbook.symbol }</h3>
            <div className="asks">
              <h3>卖</h3>
              { this.state.orderbook && <ul> { this.getOrderBookAsksSection() }</ul> }
            </div>
            <div className="bids">
              <h3>买</h3>
              { this.state.orderbook && <ul> { this.getOrderBookBidsSection() }</ul> }
            </div>
            <div className="clear"></div>
          </div>
          <div className="diskorders">
            <h3>diskorders - { this.state.diskorders && this.state.diskorders.length > 0 && this.state.diskorders[0].symbol } - { this.state.diskorders && this.state.diskorders.length }</h3>
            <div className="asks">
              <h3>卖</h3>
              { this.state.diskorders && <ul> { this.getDiskOrdersAsksSection() }</ul> }
            </div>
            <div className="bids">
              <h3>买</h3>
              { this.state.diskorders && <ul> { this.getDiskOrdersBidsSection() }</ul> }
            </div>
            <div className="clear"></div>
          </div>
          <div className="clear"></div>
          <div>
            <SockJsClient url='/_sockjs' topics={this.state.theTopics || []}
                          onMessage={(msg, _topic) => { this.onWebSocketMessage(msg, _topic); }}
                          ref={ (client) => { this.clientRef = client }} />
          </div>
        </div>
      );
    }

}

let mixin = css`&{
  .clear {
    clear: both;
    float: none;
  }
  .orderbook {
    float:left;
  }
  .orderbook .asks {
    background-color:cornflowerblue;
  }
  .orderbook .bids {
    background-color:aliceblue;
  }
  .orderbook .asks, .orderbook .bids {
    font-size: 0.625em;
    float:left;
  }
  .orderbook .asks ul, .orderbook .bids ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .diskorders, .httpnetwork {
    float:left;
  }
  .diskorders .asks {
    background-color:burlywood;
  }
  .diskorders .bids {
    background-color:antiquewhite;
  }
  .diskorders .asks, .diskorders .bids {
    font-size: 0.625em;
    float:left;
  }
  .diskorders .asks ul, .diskorders .bids ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .top-section .balance {
    margin-left: 1em;
  }
  .top-section .balance ul {
    font-size: 1.15em;
    font-weight: bold;
  }
  .top-section .balance ul li {
    width: 285px;
  }
  .top-section .account-balance ul li {
    width: 585px;
  }
  .top-section .solr-balance ul li {
    width: 385px;
  }
  .top-section .oanda, .top-section .balance {
    float: left;
  }
  .top-section .oanda ul, .top-section .balance ul, .httpnetwork ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .httpnetwork ul {
    font-size: .625em;
  }

  .httpnetwork-speed span {
    width: 55px;
    display: inline-block;
  }
  .httpNetwork-logger-items {
    font-size: .625em;
    background-color: blue;
    color: white;
  }
  .httpNetwork-logger-items span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    display: inline-block;
  }
  .httpNetwork-logger-items span.time {
    width: 75px;
    text-align: right;
  }
  .httpNetwork-logger-items span.speed {
    width: 28px;
    text-align: right;
  }
  .httpNetwork-logger-items span.method {
    width: 40px;
    text-align: right;    
    margin-right: 7px;
  }
  .httpNetwork-logger-items span.url {
    width: 650px;
  }
}`;

const IndexPage = styled(_IndexPage)`
    ${mixin}
`;

export default IndexPage;

