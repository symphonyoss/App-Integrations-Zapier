import React from 'react'

class Home extends React.Component {
	render() {
		return (
			<div>
				{this.props.children}
			</div>
		);
	}	
}
export default Home;