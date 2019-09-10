import React from 'react'
import { CSSTransition, TransitionGroup } from 'react-transition-group'

import { IconCaretDown, IconGithub, IconSpring, IconTwitter } from '../icons'

class QuickLinks extends React.Component {
  constructor(props) {
    super(props)

    this.setWrapperRef = this.setWrapperRef.bind(this)
    this.handleClickOutside = this.handleClickOutside.bind(this)

    this.state = {
      help: false,
    }
  }

  componentDidMount() {
    document.addEventListener('mousedown', this.handleClickOutside)
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handleClickOutside)
  }

  setWrapperRef(node) {
    this.wrapperRef = node
  }

  handleClickOutside(event) {
    if (this.wrapperRef && !this.wrapperRef.contains(event.target)) {
      this.setState({ help: false })
    }
  }

  render() {
    return (
      <ul className='quick-links'>
        <li>
          <a
            href='https://github.com/spring-io/start.spring.io'
            rel='noreferrer noopener'
            target='_blank'
            tabIndex='-1'
          >
            <IconGithub />
            Github
          </a>
        </li>
        <li>
          <a
            href='https://twitter.com/springboot'
            rel='noreferrer noopener'
            target='_blank'
            tabIndex='-1'
          >
            <IconTwitter />
            Twitter
          </a>
        </li>
        <li>
          <a
            href='/'
            className='dropdown'
            tabIndex='-1'
            onClick={e => {
              e.preventDefault()
              this.setState({ help: !this.state.help })
            }}
          >
            <IconSpring />
            Help
            <IconCaretDown className='caret' />
          </a>

          <TransitionGroup component={null}>
            {this.state.help && (
              <CSSTransition classNames='nav-anim' timeout={500}>
                <ul className='dropdown-menu' ref={this.setWrapperRef}>
                  <li>
                    <a
                      id='ql-help-projects'
                      target='_blank'
                      rel='noopener noreferrer'
                      href='https://spring.io/projects'
                      tabIndex='-1'
                      onClick={() => {
                        this.setState({ help: false })
                      }}
                    >
                      Spring Projects
                    </a>
                  </li>
                  <li>
                    <a
                      id='ql-help-guides'
                      target='_blank'
                      rel='noopener noreferrer'
                      tabIndex='-1'
                      href='https://spring.io/guides'
                      onClick={() => {
                        this.setState({ help: false })
                      }}
                    >
                      Spring Guides
                    </a>
                  </li>
                  <li>
                    <a
                      id='ql-help-spring-blog'
                      target='_blank'
                      rel='noopener noreferrer'
                      tabIndex='-1'
                      href='https://spring.io/blog'
                      onClick={() => {
                        this.setState({ help: false })
                      }}
                    >
                      What's New With Spring
                    </a>
                  </li>

                  <li>
                    <a
                      id='ql-help-comminty'
                      target='_blank'
                      rel='noopener noreferrer'
                      tabIndex='-1'
                      href='https://github.com/spring-projects/spring-boot/blob/b0626bca320c21928e9790fcd2c59337344f9ac5/spring-boot-project/spring-boot-starters/README.adoc'
                      onClick={() => {
                        this.setState({ help: false })
                      }}
                    >
                      Discover Community Starters
                    </a>
                  </li>
                  <li>
                    <a
                      id='ql-help-migration'
                      target='_blank'
                      rel='noopener noreferrer'
                      tabIndex='-1'
                      href='https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide'
                      onClick={() => {
                        this.setState({ help: false })
                      }}
                    >
                      Migrate from 1.5 => 2.0
                    </a>
                  </li>
                </ul>
              </CSSTransition>
            )}
          </TransitionGroup>
        </li>
      </ul>
    )
  }
}

export default QuickLinks
